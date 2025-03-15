/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.energypipe;


import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.api.utils.SafeChunkUtils;
import blusunrize.immersiveengineering.api.utils.shapes.CachedVoxelShapes;
import blusunrize.immersiveengineering.common.EventHandler;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock.IELadderBlock;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.register.IEBlocks.WoodenDecoration;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.WorldMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@EventBusSubscriber(
		modid = "immersiveengineering",
		bus = Bus.FORGE
)
public class IGEnergyPipeEntity extends IEBaseBlockEntity implements IEnergyPipe, IEBlockInterfaces.IColouredBE, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IHammerInteraction, IEBlockInterfaces.IPlacementInteraction, IEBlockInterfaces.ISelectionBounds, IEBlockInterfaces.ICollisionBounds, IEBlockInterfaces.IAdditionalDrops {
	static WorldMap<BlockPos, Set<IGEnergyPipeEntity.DirectionalEnergyOutput>> indirectConnections = new WorldMap();
	public static ArrayList<Predicate<Block>> validPipeCovers = new ArrayList();
	public static ArrayList<Predicate<Block>> climbablePipeCovers = new ArrayList();
	public Object2BooleanMap<Direction> sideConfig = new Object2BooleanOpenHashMap();
	public Block cover;
	private byte connections;
	@Nullable
	private DyeColor color;
	private final Map<Direction, ResettableCapability<IEnergyStorage>> sidedHandlers;
	private final Map<Direction, CapabilityReference<IEnergyStorage>> neighbors;
	private static final CachedVoxelShapes<IGEnergyPipeEntity.BoundingBoxKey> SHAPES = new CachedVoxelShapes<>(IGEnergyPipeEntity::getBoxes);

	public IGEnergyPipeEntity(BlockPos pos, BlockState state) {
		super(IGRegistrationHolder.ENERGY_PIPE.get(), pos, state);
		Direction[] var3 = DirectionUtils.VALUES;
		int var4 = var3.length;
		int var5;
		Direction f;
		for(var5 = 0; var5 < var4; ++var5) {
			f = var3[var5];
			this.sideConfig.put(f, true);
		}

		this.cover = Blocks.AIR;
		this.connections = 0;
		this.color = null;
		this.sidedHandlers = new EnumMap(Direction.class);
		this.neighbors = CapabilityReference.forAllNeighbors(this, ForgeCapabilities.ENERGY);
		var3 = DirectionUtils.VALUES;
		var4 = var3.length;

		for(var5 = 0; var5 < var4; ++var5) {
			f = var3[var5];
			this.sidedHandlers.put(f, this.registerCapability(new PipeEnergyHandler(this, f)));
		}

	}

	@Override
	public boolean hasVoltageLimit()
	{
		return true;
	}

	@Override
	public int getVoltageLimit(IGEnergyPipe pipe)
	{
		return pipe.getTransferLimit();
	}

	public static void initCovers() {
		validPipeCovers.add((b) -> {
			return b.defaultBlockState().is(IETags.scaffoldingAlu);
		});
		validPipeCovers.add((b) -> {
			return b.defaultBlockState().is(IETags.scaffoldingSteel);
		});
		validPipeCovers.add((input) -> {
			return input == WoodenDecoration.TREATED_SCAFFOLDING.get();
		});
		climbablePipeCovers.add((b) -> {
			return b.defaultBlockState().is(IETags.scaffoldingAlu);
		});
		climbablePipeCovers.add((b) -> {
			return b.defaultBlockState().is(IETags.scaffoldingSteel);
		});
		climbablePipeCovers.add((input) -> {
			return input == WoodenDecoration.TREATED_SCAFFOLDING.get();
		});
	}

	public static Set<DirectionalEnergyOutput> getConnectedEnergyHandlers(BlockPos node, Level world) {
		if (world.isClientSide) {
			return ImmutableSet.of();
		} else {
			// Check cache first
			Set<DirectionalEnergyOutput> cachedResult = indirectConnections.get(world, node);
			if (cachedResult != null) {
				return cachedResult;
			} else {
				ArrayList<BlockPos> openList = new ArrayList<>();
				ArrayList<BlockPos> closedList = new ArrayList<>();
				Set<DirectionalEnergyOutput> energyHandlers = Collections.newSetFromMap(new ConcurrentHashMap<>());
				openList.add(node);

				// Breadth-first search through connected pipes
				while (!openList.isEmpty() && closedList.size() < 1024) {
					BlockPos next = openList.get(0);
					openList.remove(0);

					BlockEntity pipeTile = Utils.getExistingTileEntity(world, next);
					if (!closedList.contains(next) && pipeTile instanceof IGEnergyPipeEntity) {
						closedList.add(next);

						// Check all six directions
						for (Direction fd : DirectionUtils.VALUES) {
							if (((IGEnergyPipeEntity)pipeTile).hasOutputConnection(fd)) {
								BlockPos nextPos = next.relative(fd);
								BlockEntity adjacentTile = Utils.getExistingTileEntity(world, nextPos);

								if (adjacentTile != null) {
									if (adjacentTile instanceof IGEnergyPipeEntity) {
										// Add connected pipe to open list for further exploration
										openList.add(nextPos);
									} else {
										// Check if the adjacent tile has energy capability
										LazyOptional<IEnergyStorage> handlerOptional = adjacentTile.getCapability(
												ForgeCapabilities.ENERGY, fd.getOpposite());

										handlerOptional.ifPresent(handler -> {
											// Add to energy handlers list
											energyHandlers.add(new DirectionalEnergyOutput(handler, fd, adjacentTile));
										});
									}
								}
							}
						}
					}
				}

				// Cache the result
				indirectConnections.put(world, node, energyHandlers);
				return energyHandlers;
			}
		}
	}

	public void onLoad() {
		super.onLoad();
		if (this.level != null && !this.level.isClientSide) {
			EventHandler.SERVER_TASKS.add(() -> {
				boolean changed = false;
				Direction[] var2 = DirectionUtils.VALUES;
				int var3 = var2.length;

				for(int var4 = 0; var4 < var3; ++var4) {
					Direction f = var2[var4];
					changed |= this.updateConnectionByte(f);
				}

				if (changed) {
					this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
					this.markContainingBlockForUpdate((BlockState)null);
				}

			});
		}

	}

	public void setRemovedIE() {
		super.setRemovedIE();
		if (this.level != null && !this.level.isClientSide) {
			indirectConnections.clearDimension(this.level);
		}

	}

	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if (this.level != null && !this.level.isClientSide) {
			indirectConnections.clearDimension(this.level);
		}

	}

	public void onEntityCollision(Level world, Entity entity) {
		if (entity instanceof LivingEntity&& !((LivingEntity)entity).onClimbable() && this.cover != Blocks.AIR) {
			boolean climb = false;
			Iterator var4 = climbablePipeCovers.iterator();

			while(var4.hasNext()) {
				Predicate<Block> f = (Predicate)var4.next();
				if (f != null && f.test(this.cover)) {
					climb = true;
					break;
				}
			}

			if (climb) {
				IELadderBlock.applyLadderLogic(entity);
			}
		}

	}

	public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
		int[] config = nbt.getIntArray("sideConfig");

		for(int i = 0; i < 6; ++i) {
			Direction curDir = Direction.from3DDataValue(i);
			if (i < config.length) {
				boolean connected = config[i] != 0;
				this.sideConfig.put(curDir, connected);
				if (connected) {
					this.setValidHandler(curDir);
				} else {
					this.invalidateHandler(curDir);
				}
			} else {
				this.sideConfig.put(curDir, false);
				this.invalidateHandler(curDir);
			}
		}

		Block oldCover = this.cover;
		this.cover = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("cover")));
		DyeColor oldColor = this.color;
		if (nbt.contains("color", 3)) {
			this.color = DyeColor.byId(nbt.getInt("color"));
		} else {
			this.color = null;
		}

		byte oldConns = this.connections;
		this.connections = nbt.getByte("connections");
		if (this.level != null && this.level.isClientSide && (this.connections != oldConns || this.color != oldColor || this.cover != oldCover)) {
			BlockState state = this.level.getBlockState(this.worldPosition);
			this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
		}

	}

	public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
		int[] config = new int[6];

		for(int i = 0; i < 6; ++i) {
			if (this.sideConfig.getBoolean(Direction.from3DDataValue(i))) {
				config[i] = 1;
			}
		}

		nbt.putIntArray("sideConfig", config);
		if (this.hasCover()) {
			nbt.putString("cover", ForgeRegistries.BLOCKS.getKey(this.cover).toString());
		}

		nbt.putByte("connections", this.connections);
		if (this.color != null) {
			nbt.putInt("color", this.color.getId());
		}

	}

	private void invalidateHandler(Direction side) {
		ResettableCapability<IEnergyStorage> handler = this.sidedHandlers.get(side);
		if (handler != null) {
			this.sidedHandlers.put(side, null);
			handler.reset();
		}

	}

	private void setValidHandler(Direction side) {
		ResettableCapability<IEnergyStorage> handler = this.sidedHandlers.get(side);
		if (handler == null) {
			this.sidedHandlers.put(side, this.registerCapability(new PipeEnergyHandler(this, side)));
		}
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		return capability == ForgeCapabilities.ENERGY && facing != null && this.sideConfig.getBoolean(facing) ? ((ResettableCapability)this.sidedHandlers.get(facing)).cast() : super.getCapability(capability, facing);
	}

	protected boolean hasCover() {
		return this.cover != Blocks.AIR;
	}

	public Collection<ItemStack> getExtraDrops(Player player, BlockState state) {
		return this.hasCover() ? Lists.newArrayList(new ItemStack[]{new ItemStack(this.cover)}) : null;
	}

	public void onNeighborBlockChange(BlockPos otherPos) {
		super.onNeighborBlockChange(otherPos);
		Direction dir = Direction.getNearest((float)(otherPos.getX() - this.worldPosition.getX()), (float)(otherPos.getY() - this.worldPosition.getY()), (float)(otherPos.getZ() - this.worldPosition.getZ()));
		if (this.updateConnectionByte(dir)) {
			Level world = this.getLevelNonnull();
			world.updateNeighborsAtExceptFromFacing(this.worldPosition, this.getBlockState().getBlock(), dir);
			this.markContainingBlockForUpdate((BlockState)null);
			if (!world.isClientSide) {
				indirectConnections.clearDimension(world);
			}
		}

	}

	public boolean updateConnectionByte(Direction dir) {
		if (this.level != null && !this.level.isClientSide && SafeChunkUtils.isChunkSafe(this.level, this.worldPosition.relative(dir))) {
			byte oldConn = this.connections;
			int i = dir.get3DDataValue();
			int mask = 1 << i;
			this.connections = (byte)(this.connections & ~mask);
			if (this.sideConfig.getBoolean(dir)) {
				IEnergyStorage handler = (IEnergyStorage)((CapabilityReference)this.neighbors.get(dir)).getNullable();
				if (handler != null && handler.getEnergyStored() >= 0) {
					this.connections = (byte)(this.connections | mask);
				}
			}

			return oldConn != this.connections;
		} else {
			return false;
		}
	}

	public byte getAvailableConnectionByte() {
		byte availableConnections = this.connections;
		int mask = 1;
		Direction[] var3 = DirectionUtils.VALUES;
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			Direction dir = var3[var5];
			if ((availableConnections & mask) == 0) {
				if (this.level.getBlockEntity(this.getBlockPos().relative(dir)) instanceof IGEnergyPipeEntity) {
					availableConnections = (byte)(availableConnections | mask);
				} else {
					IEnergyStorage handler = (IEnergyStorage)((CapabilityReference)this.neighbors.get(dir)).getNullable();
					if (handler != null && handler.getEnergyStored() > 0) {
						availableConnections = (byte)(availableConnections | mask);
					}
				}
			}

			mask <<= 1;
		}

		return availableConnections;
	}

	public IGEnergyPipeEntity.ConnectionStyle getConnectionStyle(Direction connection) {
		if ((this.connections & 1 << connection.get3DDataValue()) == 0) {
			return IGEnergyPipeEntity.ConnectionStyle.NO_CONNECTION;
		} else if (this.connections != 3 && this.connections != 12 && this.connections != 48) {
			IEnergyStorage handler = (IEnergyStorage)((CapabilityReference)this.neighbors.get(connection)).getNullable();
			BlockEntity con = Utils.getExistingTileEntity(this.level, this.getBlockPos().relative(connection));
			if(handler!=null&&handler.getEnergyStored() >= 0 &! (con instanceof IGEnergyPipeEntity))
			{
				return ConnectionStyle.PLUGGED;
			}
			return ConnectionStyle.FLANGE;
		} else {
			BlockEntity con = Utils.getExistingTileEntity(this.level, this.getBlockPos().relative(connection));
			if (con instanceof IGEnergyPipeEntity) {
				IGEnergyPipeEntity pipe = (IGEnergyPipeEntity)con;
				int tileConnections = pipe.connections | 1 << connection.getOpposite().get3DDataValue();
				if (this.connections == tileConnections) {
					return IGEnergyPipeEntity.ConnectionStyle.PLAIN;
				}
			} else
			{
				IEnergyStorage handler = (IEnergyStorage)((CapabilityReference)this.neighbors.get(connection)).getNullable();
				if(handler!=null&&handler.getEnergyStored() >= 0)
				{
					return ConnectionStyle.PLUGGED;
				}
			}

			return IGEnergyPipeEntity.ConnectionStyle.FLANGE;
		}
	}

	public void toggleSide(Direction side) {
		boolean newSideConnected = !this.sideConfig.getBoolean(side);
		this.setSide(side, newSideConnected);
	}

	public void setSide(Direction side, boolean connectable) {
		this.setSide(side, connectable, true);
	}

	public void setSide(Direction side, boolean connectable, boolean firstPipe) {
		this.sideConfig.put(side, connectable);
		if (connectable) {
			this.setValidHandler(side);
		} else {
			this.invalidateHandler(side);
		}

		this.setChanged();
		if (firstPipe) {
			BlockEntity neighborTile = this.level.getBlockEntity(this.getBlockPos().relative(side));
			if (neighborTile instanceof IGEnergyPipeEntity) {
				((IGEnergyPipeEntity)neighborTile).setSide(side.getOpposite(), connectable, false);
			}

			this.updateConnectionByte(side);
		}

		this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 0, 0);
	}

	public boolean triggerEvent(int id, int arg) {
		if (id == 0) {
			this.markContainingBlockForUpdate((BlockState)null);
			return true;
		} else {
			return false;
		}
	}

	public VoxelShape getCollisionShape(CollisionContext ctx) {
		return SHAPES.get(new IGEnergyPipeEntity.BoundingBoxKey(false, this));
	}

	public VoxelShape getSelectionShape(@Nullable CollisionContext ctx) {
		boolean hammer = ctx != null && ctx.isHoldingItem(Tools.HAMMER.get());
		return SHAPES.get(new IGEnergyPipeEntity.BoundingBoxKey(hammer, this));
	}

	private static List<AABB> getBoxes(IGEnergyPipeEntity.BoundingBoxKey key) {
		List<AABB> list = Lists.newArrayList();
		if (!key.showToolView && key.hasCover) {
			list.add((new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).inflate(-0.03125));
			return list;
		} else {
			byte availableConnections = key.availableConnections;
			byte activeConnections = key.connections;
			double[] baseAABB = key.hasCover ? new double[]{0.002, 0.998, 0.002, 0.998, 0.002, 0.998} : new double[]{0.25, 0.75, 0.25, 0.75, 0.25, 0.75};
			Direction[] var5 = DirectionUtils.VALUES;
			int var6 = var5.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				Direction d = var5[var7];
				int i = d.get3DDataValue();
				if ((availableConnections & 1) == 1 && ((activeConnections & 1) == 1 || key.showToolView)) {
					list.add(new AABB(i == 4 ? 0.0 : (i == 5 ? 0.75 : 0.25), i == 0 ? 0.0 : (i == 1 ? 0.75 : 0.25), i == 2 ? 0.0 : (i == 3 ? 0.75 : 0.25), i == 4 ? 0.25 : (i == 5 ? 1.0 : 0.75), i == 0 ? 0.25 : (i == 1 ? 1.0 : 0.75), i == 2 ? 0.25 : (i == 3 ? 1.0 : 0.75)));
					if (key.connectionStyles.get(d) == IGEnergyPipeEntity.ConnectionStyle.FLANGE) {
						list.add(new AABB(
								i == 4 ? 0.0 : (i == 5 ? 0.875 : 0.21875),
								i == 0 ? 0.0 : (i == 1 ? 0.875 : 0.21875),
								i == 2 ? 0.0 : (i == 3 ? 0.875 : 0.21875),
								i == 4 ? 0.125 : (i == 5 ? 1.0 : 0.78125),
								i == 0 ? 0.125 : (i == 1 ? 1.0 : 0.78125),
								i == 2 ? 0.125 : (i == 3 ? 1.0 : 0.78125)
						));
					}
				}

				availableConnections = (byte)(availableConnections >> 1);
				activeConnections = (byte)(activeConnections >> 1);
			}

			list.add(new AABB(baseAABB[4], baseAABB[0], baseAABB[2], baseAABB[5], baseAABB[1], baseAABB[3]));
			return list;
		}
	}

	public int getRenderColour(int tintIndex) {
		return 16777215;
	}

	public void dropCover(Player player) {
		if (!this.level.isClientSide && this.hasCover() && this.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
			ItemEntity entityitem = player.drop(new ItemStack(this.cover), false);
			if (entityitem != null) {
				entityitem.setNoPickUpDelay();
			}
		}

	}

	public boolean interact(Direction side, Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
		if (heldItem.isEmpty() && player.isShiftKeyDown() && this.hasCover()) {
			if (!player.level().isClientSide) {
				this.dropCover(player);
				this.cover = Blocks.AIR;
				this.markContainingBlockForUpdate((BlockState)null);
				this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 255, 0);
				this.markChunkDirty();
			}

			return true;
		} else {
			return !heldItem.isEmpty() && !player.isShiftKeyDown() ? this.setColorOrCoverFrom(heldItem, player) : false;
		}
	}

	private boolean setColorOrCoverFrom(ItemStack heldItem, Player player) {
		DyeColor heldDye = Utils.getDye(heldItem);
		if (heldDye != null) {
			if (!player.level().isClientSide) {
				this.color = heldDye;
				this.markChunkDirty();
				this.markContainingBlockForUpdate((BlockState)null);
				this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 255, 0);
			}

			return true;
		} else {
			Block heldBlock = Block.byItem(heldItem.getItem());
			if (heldBlock == Blocks.AIR) {
				return false;
			} else {
				Iterator var5 = validPipeCovers.iterator();

				Predicate func;
				do {
					if (!var5.hasNext()) {
						return false;
					}

					func = (Predicate)var5.next();
				} while(!func.test(heldBlock) || this.cover == heldBlock);

				if (!player.level().isClientSide) {
					this.dropCover(player);
					this.cover = heldBlock;
					this.markChunkDirty();
					if (!player.getAbilities().instabuild) {
						heldItem.shrink(1);
					}

					this.markContainingBlockForUpdate((BlockState)null);
					this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 255, 0);
				}

				return true;
			}
		}
	}

	public static enum ConnectionStyle {
		NO_CONNECTION,
		PLAIN,
		PLUGGED,
		FLANGE;

		private ConnectionStyle() {
		}
	}

	public boolean hammerUseSide(Direction side, Player player, InteractionHand hand, Vec3 hitVec) {
		if (this.level.isClientSide) {
			return true;
		} else {
			hitVec = hitVec.subtract(Vec3.atLowerCornerOf(this.worldPosition));
			Direction fd = side;
			List<AABB> boxes = getBoxes(new IGEnergyPipeEntity.BoundingBoxKey(true, this));
			Iterator var7 = boxes.iterator();

			label35:
			while(var7.hasNext()) {
				AABB box = (AABB)var7.next();
				if (box.inflate(0.002).contains(hitVec)) {
					Direction[] var9 = DirectionUtils.VALUES;
					int var10 = var9.length;
					int var11 = 0;

					while(true) {
						if (var11 >= var10) {
							break label35;
						}

						Direction d = var9[var11];
						Vec3 testVec = new Vec3(0.5 + 0.5 * (double)d.getStepX(), 0.5 + 0.5 * (double)d.getStepY(), 0.5 + 0.5 * (double)d.getStepZ());
						if (box.inflate(0.002).contains(testVec)) {
							fd = d;
							break label35;
						}

						++var11;
					}
				}
			}

			if (fd != null) {
				this.toggleSide(fd);
				this.markContainingBlockForUpdate((BlockState)null);
				indirectConnections.clearDimension(this.level);
				return true;
			} else {
				return false;
			}
		}
	}

	public void onBEPlaced(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		if (!level.isClientSide) {
			if (ctx.getPlayer() != null) {
				InteractionHand otherHand = ctx.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
				this.setColorOrCoverFrom(ctx.getPlayer().getItemInHand(otherHand), ctx.getPlayer());
			}

			BlockPos pos = ctx.getClickedPos();
			Direction[] var4 = Direction.values();
			int var5 = var4.length;

			for(int var6 = 0; var6 < var5; ++var6) {
				Direction dir = var4[var6];
				BlockEntity var9 = level.getBlockEntity(pos.relative(dir));
				if (var9 instanceof IGEnergyPipeEntity) {
					IGEnergyPipeEntity neighborPipe = (IGEnergyPipeEntity)var9;
					if (neighborPipe.color != this.color || !neighborPipe.sideConfig.getBoolean(dir.getOpposite())) {
						this.setSide(dir, false);
					}
				}
			}

		}
	}

	public boolean hasOutputConnection(Direction side) {
		return this.sideConfig.getBoolean(side);
	}

	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload ev) {
		if (!ev.getLevel().isClientSide()) {
			LevelAccessor var2 = ev.getLevel();
			if (var2 instanceof Level) {
				Level level = (Level)var2;
				indirectConnections.clearDimension(level);
			}
		}

	}

	@Nullable
	public DyeColor getColor() {
		return this.color;
	}

	static class PipeEnergyHandler implements IEnergyStorage {
		private static final Random CURRENT_TICK_RANDOM = new Random();
		IGEnergyPipeEntity pipe;
		Direction facing;

		public PipeEnergyHandler(IGEnergyPipeEntity pipe, Direction facing) {
			this.pipe = pipe;
			this.facing = facing;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (maxReceive <= 0) {
				return 0;
			}

			Set<DirectionalEnergyOutput> outputList = IGEnergyPipeEntity.getConnectedEnergyHandlers(this.pipe.getBlockPos(), this.pipe.level);
			if (outputList.size() < 1) {
				return 0;
			}

			BlockPos sourcePos = new BlockPos(this.pipe.getBlockPos().relative(this.facing));
			int sum = 0;
			HashMap<DirectionalEnergyOutput, Integer> sorting = new HashMap<>();

			// First pass: Identify all possible outputs and how much each can accept
			for (DirectionalEnergyOutput output : outputList) {
				BlockPos outputPos = output.containingTile().getBlockPos();
				// Skip the source block we received energy from and invalid outputs
				if (!outputPos.equals(sourcePos) &&
						this.pipe.level.hasChunkAt(outputPos) &&
						!this.pipe.equals(output.containingTile()) &&
						output.output().canReceive()) {

					int transferableAmount = getTransferableAmount(output.containingTile());
					int amountToTry = Math.min(transferableAmount, maxReceive);

					int accepted = 0;
					if (output.limitVoltage()) {
						// Standard voltage transfer
						accepted = output.output().receiveEnergy(amountToTry, true);
					}

					if (accepted > 0) {
						sorting.put(output, accepted);
						sum += accepted;
					}
				}
			}

			if (sum <= 0) {
				return 0;
			}

			// Second pass: Actually transfer the energy
			int energyTransferred = 0;
			for (DirectionalEnergyOutput output : sorting.keySet()) {
				int amount = sorting.get(output);

				// Handle case where total possible distribution exceeds available energy
				if (sum > maxReceive) {
					int transferableAmount = getTransferableAmount(output.containingTile());
					int amountToTransfer = Math.min(transferableAmount, maxReceive - energyTransferred);

					// Calculate proportional amount based on this output's capacity
					float priority = (float)amount / (float)sum;
					amount = (int)Math.ceil(Mth.clamp(amount, 1,
							Math.min(maxReceive * priority, amountToTransfer)));
					amount = Math.min(amount, maxReceive - energyTransferred);
				}

				// Perform the actual transfer if not simulating
				int transferred = 0;
				if (output.limitVoltage()) {
					transferred = output.output().receiveEnergy(amount, simulate);
				}

				energyTransferred += transferred;
				maxReceive -= transferred;

				if (maxReceive <= 0) {
					break;
				}
			}

			return energyTransferred;
		}

		private int getTransferableAmount(BlockEntity target) {
			Block block = target.getBlockState().getBlock();
			if(block instanceof IGEnergyPipe cable) return pipe.getTransferableAmount(cable);
			return 4096;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (maxExtract <= 0) {
				return 0;
			}

			Level world = this.pipe.getLevelNonnull();
			List<DirectionalEnergyOutput> outputList = new ArrayList<>(
					IGEnergyPipeEntity.getConnectedEnergyHandlers(this.pipe.getBlockPos(), world));
			BlockPos sourcePos = new BlockPos(this.pipe.getBlockPos().relative(this.facing));

			// Remove the block we're extracting to from potential sources
			outputList.removeIf(output -> sourcePos.equals(output.containingTile().getBlockPos()));

			if (outputList.size() < 1) {
				return 0;
			}

			// Randomly select a connected energy source to extract from
			CURRENT_TICK_RANDOM.setSeed(HashCommon.mix(world.getGameTime()));
			int chosen = outputList.size() == 1 ? 0 : CURRENT_TICK_RANDOM.nextInt(outputList.size());
			DirectionalEnergyOutput output = outputList.get(chosen);

			if (!output.output().canExtract()) {
				return 0;
			}

			// Check how much energy is available to extract
			int available = output.output().extractEnergy(maxExtract, true);
			BlockEntity extractingBE = SafeChunkUtils.getSafeBE(world, this.pipe.getBlockPos().relative(this.facing));
			int limit = getTransferableAmount(extractingBE);
			int actualExtract = Math.min(limit, Math.min(available, maxExtract));

			// Perform the actual extraction if not simulating
			return output.output().extractEnergy(actualExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			// Energy pipes don't store energy, they only transfer it
			return 0;
		}

		@Override
		public int getMaxEnergyStored() {
			return Integer.MAX_VALUE;// Let's give it unlimited transfer rate
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
	}

	private static class BoundingBoxKey {
		private final boolean showToolView;
		private final byte connections;
		private final byte availableConnections;
		private final boolean hasCover;
		private final Map<Direction, IGEnergyPipeEntity.ConnectionStyle> connectionStyles = new EnumMap(Direction.class);

		private BoundingBoxKey(boolean showToolView, IGEnergyPipeEntity te) {
			this.showToolView = showToolView;
			this.connections = te.connections;
			this.availableConnections = te.getAvailableConnectionByte();
			this.hasCover = te.hasCover();
			Direction[] var3 = DirectionUtils.VALUES;
			int var4 = var3.length;

			for(int var5 = 0; var5 < var4; ++var5) {
				Direction d = var3[var5];
				this.connectionStyles.put(d, te.getConnectionStyle(d));
			}

		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				IGEnergyPipeEntity.BoundingBoxKey that = (IGEnergyPipeEntity.BoundingBoxKey)o;
				return this.showToolView == that.showToolView && this.connections == that.connections && this.availableConnections == that.availableConnections && this.hasCover == that.hasCover && this.connectionStyles.equals(that.connectionStyles);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.showToolView, this.connections, this.availableConnections, this.hasCover, this.connectionStyles});
		}
	}

	public static record DirectionalEnergyOutput(IEnergyStorage output, Direction direction, BlockEntity containingTile) {
		public DirectionalEnergyOutput(IEnergyStorage output, Direction direction, BlockEntity containingTile) {
			this.output = output;
			this.direction = direction;
			this.containingTile = containingTile;
		}

		boolean limitVoltage() {
			BlockEntity var2 = this.containingTile;
			if (var2 instanceof IEnergyPipe pipe) {
				return pipe.hasVoltageLimit();
			}
			return true;
		}

		public IEnergyStorage output() {
			return this.output;
		}

		public Direction direction() {
			return this.direction;
		}

		public BlockEntity containingTile() {
			return this.containingTile;
		}
	}
}