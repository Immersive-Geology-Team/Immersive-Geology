/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import com.igteam.immersivegeology.common.menu.IGMetalDetectorMenu;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator.ChunkDeposit;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IGMetalDetectorEntity extends IEBaseBlockEntity implements IEServerTickableBE, MenuProvider
{
	public static final int RADAR_RADIUS = 8;
	public static final int RADAR_SIZE = RADAR_RADIUS*2+1;

	public static final int ENERGY_CAPACITY = 32000;
	public static final int ENERGY_MAX_INPUT = 256;

	public static final int SCAN_INTERVAL = 6000;

	public static final int VERIFY_INTERVAL = 10;

	public static final int IDLE_VERIFY_INTERVAL = 6000;

	public static final int IDLE_BATCH = 8;

	public static final int ENERGY_PER_TICK = 16;
	public static final int ENERGY_PER_TICK_IDLE = 1;

	private static final int[] SPIRAL = buildSpiral();

	private static final TicketType<ChunkPos> VERIFY_TICKET =
			TicketType.create("ig_detector_verify", java.util.Comparator.comparingLong(ChunkPos::toLong));

	@Nullable
	private static volatile List<MaterialInterface<?>> knownMaterials;

	public final MutableEnergyStorage energyStorage = new MutableEnergyStorage(ENERGY_CAPACITY, ENERGY_MAX_INPUT, 0);
	private final ResettableCapability<IEnergyStorage> energyCap = registerCapability(energyStorage);

	private byte[] radar = new byte[RADAR_SIZE*RADAR_SIZE];

	private byte[] grade = new byte[RADAR_SIZE*RADAR_SIZE];

	private final int[] counts = new int[RADAR_SIZE*RADAR_SIZE];

	public static final int SLOT_MAP_IN = 0;
	public static final int SLOT_MAP_OUT = 1;

	private final ItemStackHandler mapSlots = new ItemStackHandler(2)
	{
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack)
		{
			return slot==SLOT_MAP_IN&&stack.getItem() instanceof MapItem;
		}

		@Override
		public int getSlotLimit(int slot)
		{
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot)
		{
			setChanged();
			if(slot==SLOT_MAP_IN) mapDirty = true;
		}
	};

	private boolean running = false;

	private boolean idle = false;

	private boolean changedThisPass = false;
	private boolean lastRedstone = false;

	private int burstRemaining = 0;

	private int scanTimer = SCAN_INTERVAL;
	private int verifyTimer = 0;
	private int spiralCursor = 0;
	private boolean mapDirty = false;

	private int pendingChunkCell = -1;
	@Nullable
	private ChunkPos pendingChunkPos;

	private boolean displayDirty = false;
	private int syncTimer = 0;
	@Nullable
	private CompletableFuture<List<ChunkDeposit>> pendingScan;

	public IGMetalDetectorEntity(BlockEntityType<IGMetalDetectorEntity> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public void tickServer()
	{
		if(level==null) return;

		collectFinishedScan();

		boolean redstone = isRedstoneSignalled();
		if(redstone!=lastRedstone)
		{
			// Flicking the redstone signal forces a recheck.
			lastRedstone = redstone;
			wake();
		}

		int draw = idle?ENERGY_PER_TICK_IDLE: ENERGY_PER_TICK;
		boolean canRun = !redstone&&energyStorage.getEnergyStored() >= draw;
		if(canRun!=running)
		{
			running = canRun;
			markContainingBlockForUpdate(null);
		}
		if(!canRun) return;

		// The storage is built with no extract rate so cables cannot pull power back out... looking at you issue #219
		energyStorage.setStoredEnergy(Math.max(0, energyStorage.getEnergyStored()-draw));

		if(idle&&burstRemaining <= 0)
		{
			if(++verifyTimer >= IDLE_VERIFY_INTERVAL)
			{
				verifyTimer = 0;
				burstRemaining = IDLE_BATCH;
			}
		}
		else if(++verifyTimer >= VERIFY_INTERVAL)
		{
			verifyTimer = 0;
			verifyNextCell();
			if(idle) burstRemaining--;
		}

		if(mapDirty)
		{
			mapDirty = false;
			processMap();
		}

		if(displayDirty&&++syncTimer >= VERIFY_INTERVAL*4)
		{
			displayDirty = false;
			syncTimer = 0;
			markContainingBlockForUpdate(null);
		}

		if(pendingScan!=null) return;
		if(++scanTimer < SCAN_INTERVAL) return;
		scanTimer = 0;
		pendingScan = IGVeinLocator.scanDepositsAsync((ServerLevel)level, new ChunkPos(getBlockPos()), RADAR_RADIUS);
	}

	private void verifyNextCell()
	{
		if(!(level instanceof ServerLevel serverLevel)) return;

		int cell = pendingChunkCell >= 0?pendingChunkCell: nextCellToVerify();
		if(cell < 0) return;

		MaterialInterface<?> material = seedMaterialForCell(cell);
		if(material==null)
		{
			releasePendingChunk();
			return;
		}

		ChunkPos origin = new ChunkPos(getBlockPos());
		int chunkX = origin.x+(cell%RADAR_SIZE)-RADAR_RADIUS;
		int chunkZ = origin.z+(cell/RADAR_SIZE)-RADAR_RADIUS;

		LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
		if(chunk==null)
		{
			pendingChunkCell = cell;
			pendingChunkPos = new ChunkPos(chunkX, chunkZ);
			serverLevel.getChunkSource().addRegionTicket(VERIFY_TICKET, pendingChunkPos, 0, pendingChunkPos);
			return;
		}

		releasePendingChunk();

		TagKey<Block> tag = material.getBlockMaterialTag();
		int found = IGVeinLocator.countMatching(chunk, state -> state.is(tag), scanMinY(), scanMaxY());
		applyCount(cell, found);
	}

	private void releasePendingChunk()
	{
		if(pendingChunkPos!=null&&level instanceof ServerLevel serverLevel)
		{
			serverLevel.getChunkSource().removeRegionTicket(VERIFY_TICKET, pendingChunkPos, 0, pendingChunkPos);
		}
		pendingChunkPos = null;
		pendingChunkCell = -1;
	}

	@Override
	public void setRemovedIE()
	{
		releasePendingChunk();
		super.setRemovedIE();
	}

	@Override
	public void onChunkUnloaded()
	{
		releasePendingChunk();
		super.onChunkUnloaded();
	}

	private int nextCellToVerify()
	{
		for(int step = 0; step < SPIRAL.length; step++)
		{
			int cell = SPIRAL[spiralCursor];
			spiralCursor++;
			if(spiralCursor >= SPIRAL.length)
			{
				spiralCursor = 0;
				if(!changedThisPass&&!idle)
				{
					idle = true;
					displayDirty = true;
				}
				changedThisPass = false;
			}
			if((radar[cell]&0xFF)!=0) return cell;
		}
		return -1;
	}

	private void applyCount(int cell, int found)
	{
		counts[cell] = found;
		byte previousGrade = grade[cell];
		grade[cell] = (byte)DepositGrade.of(found).ordinal();

		if(previousGrade!=grade[cell])
		{
			if(idle) wake();
			changedThisPass = true;
			mapDirty = true;
			displayDirty = true;
		}
	}

	private int scanMinY()
	{
		return level!=null?level.getMinBuildHeight(): -64;
	}

	private int scanMaxY()
	{
		return level!=null?level.getMaxBuildHeight()-1: 320;
	}

	private static int[] buildSpiral()
	{
		Integer[] order = new Integer[RADAR_SIZE*RADAR_SIZE];
		for(int i = 0; i < order.length; i++) order[i] = i;
		java.util.Arrays.sort(order, java.util.Comparator.comparingInt(index -> {
			int dx = Math.abs(index%RADAR_SIZE-RADAR_RADIUS);
			int dz = Math.abs(index/RADAR_SIZE-RADAR_RADIUS);
			// Chebyshev distance, so cells come in square rings rather than circles :)
			return Math.max(dx, dz);
		}));
		int[] spiral = new int[order.length];
		for(int i = 0; i < order.length; i++) spiral[i] = order[i];
		return spiral;
	}

	public ItemStackHandler getMapSlots()
	{
		return mapSlots;
	}

	private void processMap()
	{
		if(!(level instanceof ServerLevel serverLevel)) return;

		ItemStack pending = mapSlots.getStackInSlot(SLOT_MAP_IN);
		if(pending.isEmpty()||!(pending.getItem() instanceof MapItem)) return;
		if(!mapSlots.getStackInSlot(SLOT_MAP_OUT).isEmpty()) return;

		MapItemSavedData mapData = MapItem.getSavedData(pending, serverLevel);
		if(mapData==null) return;
		if(!hasAnythingToRecord()) return;

		ItemStack stamped = pending.copy();
		stampMap(serverLevel, mapData, stamped);
		mapSlots.setStackInSlot(SLOT_MAP_OUT, stamped);
		mapSlots.setStackInSlot(SLOT_MAP_IN, ItemStack.EMPTY);
		setChanged();
	}

	private boolean hasAnythingToRecord()
	{
		for(int cell = 0; cell < radar.length; cell++)
		{
			if((radar[cell]&0xFF)!=0&&DepositGrade.byId(grade[cell]&0xFF).hasDeposit()) return true;
		}
		return false;
	}

	private void stampMap(ServerLevel serverLevel, MapItemSavedData mapData, ItemStack stack)
	{
		String prefix = IGDepositMapMarks.prefixFor(getBlockPos().asLong());
		for(int cell = 0; cell < radar.length; cell++)
		{
			mapData.removeDecoration(prefix+cell);
		}

		ChunkPos origin = new ChunkPos(getBlockPos());
		List<IGDepositMapMarks.Mark> marks = new ArrayList<>();
		for(int cell = 0; cell < radar.length; cell++)
		{
			DepositGrade cellGrade = DepositGrade.byId(grade[cell]&0xFF);
			MaterialInterface<?> material = materialForCell(cell);
			if(material==null||!cellGrade.hasDeposit()) continue;

			ChunkPos marked = new ChunkPos(
					origin.x+(cell%RADAR_SIZE)-RADAR_RADIUS,
					origin.z+(cell/RADAR_SIZE)-RADAR_RADIUS);

			Component name = Component.translatable("gui.immersivegeology.metal_detector.marker",
					material.getTranslation(),
					Component.translatable("gui.immersivegeology.metal_detector.grade."
							+cellGrade.name().toLowerCase(java.util.Locale.ROOT)));

			marks.add(new IGDepositMapMarks.Mark(prefix+cell, markerFor(cellGrade),
					marked.getMiddleBlockX(), marked.getMiddleBlockZ(), name));
		}

		for(IGDepositMapMarks.Mark mark : marks)
		{
			mapData.addDecoration(mark.type(), serverLevel, mark.id(), mark.x(), mark.z(), 180.0, mark.name());
		}
		// The saved data never writes these to disk, so the item has to carry them.
		IGDepositMapMarks.write(stack, prefix, marks);
	}

	private static MapDecoration.Type markerFor(DepositGrade grade)
	{
		return switch(grade)
				{
					case RICH -> MapDecoration.Type.TARGET_POINT;
					case NORMAL -> MapDecoration.Type.RED_MARKER;
					case POOR -> MapDecoration.Type.BLUE_MARKER;
					default -> Type.RED_X;
				};
	}

	private void collectFinishedScan()
	{
		if(pendingScan==null||!pendingScan.isDone()) return;

		List<ChunkDeposit> deposits;
		try
		{
			deposits = pendingScan.join();
		}
		catch(Exception ex)
		{
			IGLib.IG_LOGGER.warn("Metal detector sweep failed: {}", ex.toString());
			deposits = List.of();
		}
		pendingScan = null;

		byte[] updated = new byte[RADAR_SIZE*RADAR_SIZE];
		ChunkPos origin = new ChunkPos(getBlockPos());
		List<MaterialInterface<?>> known = knownMaterials();
		for(ChunkDeposit deposit : deposits)
		{
			int cellX = deposit.chunk().x-origin.x+RADAR_RADIUS;
			int cellZ = deposit.chunk().z-origin.z+RADAR_RADIUS;
			if(cellX < 0||cellX >= RADAR_SIZE||cellZ < 0||cellZ >= RADAR_SIZE) continue;

			int index = known.indexOf((MaterialInterface<?>)deposit.entry());
			// Byte-packed for the sync, so a material list longer than 255 would need widening.
			if(index < 0||index >= 255) continue;
			updated[cellZ*RADAR_SIZE+cellX] = (byte)(index+1);
		}

		byte[] updatedGrades = new byte[grade.length];
		boolean moved = false;
		for(int cell = 0; cell < updated.length; cell++)
		{
			if(updated[cell]==radar[cell]) updatedGrades[cell] = grade[cell];
			else
			{
				counts[cell] = 0;
				moved = true;
			}
		}

		radar = updated;
		grade = updatedGrades;
		if(moved)
		{
			mapDirty = true;
			markContainingBlockForUpdate(null);
		}
	}

	public Direction redstoneSide()
	{
		BlockState state = getBlockState();
		if(!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return Direction.EAST;
		return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
	}

	private boolean isRedstoneSignalled()
	{
		if(level==null) return false;
		Direction side = redstoneSide();
		return level.getSignal(getBlockPos().relative(side), side) > 0;
	}

	private void wake()
	{
		idle = false;
		burstRemaining = 0;
		changedThisPass = false;
		verifyTimer = 0;
		scanTimer = SCAN_INTERVAL;
		displayDirty = true;
	}
	public boolean isSweeping()
	{
		return running&&(!idle||burstRemaining > 0);
	}

	public boolean isIdle()
	{
		return idle;
	}

	public int getProgress()
	{
		if(isDormant()) return Math.min(100, verifyTimer*100/IDLE_VERIFY_INTERVAL);
		return SPIRAL.length==0?0: spiralCursor*100/SPIRAL.length;
	}

	public int getTicksToNextCheck()
	{
		return isDormant()?Math.max(0, IDLE_VERIFY_INTERVAL-verifyTimer): 0;
	}

	private boolean isDormant()
	{
		return idle&&burstRemaining <= 0;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability==ForgeCapabilities.ENERGY&&acceptsEnergyOn(side)) return energyCap.cast();
		return super.getCapability(capability, side);
	}

	public boolean acceptsEnergyOn(@Nullable Direction side)
	{
		if(side==null) return true;
		if(side.getAxis().isVertical()) return false;
		return side!=redstoneSide();
	}

	@Override
	public @NotNull Component getDisplayName()
	{
		return Component.translatable("block.immersivegeology.metal_detector");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player)
	{
		return new IGMetalDetectorMenu(id, inventory, this);
	}

	public byte[] getRadar()
	{
		return radar;
	}

	@Nullable
	public MaterialInterface<?> getRadarMaterial(int cellX, int cellZ)
	{
		if(cellX < 0||cellX >= RADAR_SIZE||cellZ < 0||cellZ >= RADAR_SIZE) return null;
		int cell = cellZ*RADAR_SIZE+cellX;
		if(DepositGrade.byId(grade[cell]&0xFF)==DepositGrade.EMPTY) return null;
		return seedMaterialForCell(cell);
	}

	@Nullable
	private MaterialInterface<?> seedMaterialForCell(int cell)
	{
		if(cell < 0||cell >= radar.length) return null;
		int packed = radar[cell]&0xFF;
		if(packed==0) return null;
		List<MaterialInterface<?>> known = knownMaterials();
		int index = packed-1;
		return index < known.size()?known.get(index): null;
	}

	private static List<MaterialInterface<?>> knownMaterials()
	{
		List<MaterialInterface<?>> cached = knownMaterials;
		if(cached==null) knownMaterials = cached = List.copyOf(IGLib.getGeneratedMaterials());
		return cached;
	}

	public boolean isRunning()
	{
		return running;
	}

	public DepositGrade getGrade(int cellX, int cellZ)
	{
		if(cellX < 0||cellX >= RADAR_SIZE||cellZ < 0||cellZ >= RADAR_SIZE) return DepositGrade.UNVERIFIED;
		return DepositGrade.byId(grade[cellZ*RADAR_SIZE+cellX]&0xFF);
	}

	@Nullable
	private MaterialInterface<?> materialForCell(int cell)
	{
		return getRadarMaterial(cell%RADAR_SIZE, cell/RADAR_SIZE);
	}

	public boolean hasVisibleDeposits()
	{
		for(int cell = 0; cell < radar.length; cell++)
		{
			if(materialForCell(cell)!=null) return true;
		}
		return false;
	}


	@Override
	public void readCustomNBT(CompoundTag tag, boolean descPacket)
	{
		// idle is deliberately not saved. A detector that has just loaded has no idea whether the ground changed
		// while it was unloaded, so it always comes back surveying and idles again on its own if nothing has.
		energyStorage.setStoredEnergy(tag.getInt("energy"));
		running = tag.getBoolean("running");

		if(tag.contains("maps")) mapSlots.deserializeNBT(tag.getCompound("maps"));
		if(descPacket)
		{
			byte[] storedRadar = tag.getByteArray("radar");
			if(storedRadar.length==radar.length) radar = storedRadar;
			byte[] storedGrades = tag.getByteArray("grade");
			if(storedGrades.length==grade.length) grade = storedGrades;
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag tag, boolean descPacket)
	{
		tag.putInt("energy", energyStorage.getEnergyStored());
		tag.putBoolean("running", running);
		tag.put("maps", mapSlots.serializeNBT());

		// The sweep is cheap to redo and would only bloat the region file, so it rides the description packet
		// to whoever is watching and is never saved.
		if(descPacket)
		{
			tag.putByteArray("radar", radar);
			tag.putByteArray("grade", grade);
		}
	}
}
