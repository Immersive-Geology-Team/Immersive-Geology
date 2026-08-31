package com.igteam.immersivegeology.common.block.entity.cable;


import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
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
import com.igteam.immersivegeology.common.block.helper.MultiblockCapabilityReference;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import java.util.function.Predicate;

@EventBusSubscriber(
		modid = "immersivegeology",
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
	private final Map<Direction, MultiblockCapabilityReference<IEnergyStorage>> neighbors;
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
		this.sidedHandlers = new EnumMap<>(Direction.class);
		this.neighbors = MultiblockCapabilityReference.forAllNeighbors(this, ForgeCapabilities.ENERGY);

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

	/** Plain loop rather than a stream - this runs on every energy transfer. */
	private static boolean allValid(Set<DirectionalEnergyOutput> outputs) {
		for (DirectionalEnergyOutput output : outputs) {
			if (!output.isValid()) {
				return false;
			}
		}

		return true;
	}

	public static Set<DirectionalEnergyOutput> getConnectedEnergyHandlers(BlockPos node, Level world) {
		if (world.isClientSide) {
			return ImmutableSet.of();
		} else {
			// Check cache first. The cached entries hold hard references to capability handlers and
			// block entities, and nothing tells us when a multiblock disassembles, so re-validate
			// before handing them out rather than pushing energy into an orphaned buffer.
			Set<DirectionalEnergyOutput> cachedResult = indirectConnections.get(world, node);
			if (cachedResult != null && allValid(cachedResult)) {
				return cachedResult;
			} else {
				if (cachedResult != null) indirectConnections.clearDimension(world);
				ArrayDeque<BlockPos> openList = new ArrayDeque<>();
				Set<BlockPos> closedList = new HashSet<>();
				// Keyed on the resolved handler so a multiblock that exposes one buffer at several
				// CapabilityPositions (the Rotary Kiln exposes the same storage at three) is counted once.
				Map<IEnergyStorage, DirectionalEnergyOutput> energyHandlers = new IdentityHashMap<>();
				openList.add(node);

				// Breadth-first search through connected pipes
				while (!openList.isEmpty() && closedList.size() < 1024) {
					BlockPos next = openList.poll();

					BlockEntity pipeTile = Utils.getExistingTileEntity(world, next);
					if (closedList.add(next) && pipeTile instanceof IGEnergyPipeEntity) {
						// Check all six directions
						for (Direction fd : DirectionUtils.VALUES) {
							if (((IGEnergyPipeEntity)pipeTile).hasOutputConnection(fd)) {
								BlockPos nextPos = next.relative(fd);
								BlockEntity adjacentTile = Utils.getExistingTileEntity(world, nextPos);

								if (adjacentTile != null) {
									if (adjacentTile instanceof IGEnergyPipeEntity) {
										// Add connected pipe to open list for further exploration
										if (!closedList.contains(nextPos)) openList.add(nextPos);
									} else {
										// Check if the adjacent tile has energy capability
										LazyOptional<IEnergyStorage> handlerOptional = adjacentTile.getCapability(
												ForgeCapabilities.ENERGY, fd.getOpposite());

										handlerOptional.ifPresent(handler -> energyHandlers.putIfAbsent(handler,
												new DirectionalEnergyOutput(handlerOptional, fd, adjacentTile)));
									}
								}
							}
						}
					}
				}

				// Cache the result
				Set<DirectionalEnergyOutput> result = ImmutableSet.copyOf(energyHandlers.values());
				indirectConnections.put(world, node, result);
				return result;
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

	/**
	 * All six handlers are registered once in the constructor. Toggling a side only resets the
	 * capability so neighbours re-query it - dropping the entry and calling registerCapability again
	 * would grow {@code IEBaseBlockEntity}'s capability list every time a side is hammered.
	 */
	private void resetHandler(Direction side) {
		ResettableCapability<IEnergyStorage> handler = this.sidedHandlers.get(side);
		if (handler != null) {
			handler.reset();
		}
	}

	private void invalidateHandler(Direction side) {
		this.resetHandler(side);
	}

	private void setValidHandler(Direction side) {
		this.resetHandler(side);
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ENERGY && facing != null && this.sideConfig.getBoolean(facing)) {
			ResettableCapability<IEnergyStorage> handler = this.sidedHandlers.get(facing);
			if (handler != null) {
				return handler.cast();
			}
		}

		return super.getCapability(capability, facing);
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
				// Connect to anything that exposes an energy capability. The old guard also tested
				// "getEnergyStored() >= 0 || !(handler instanceof IGUndefinedEnergyInterface)", which is
				// always true - keeping the behaviour, dropping the dead condition.
				MultiblockCapabilityReference<IEnergyStorage> neighbor = this.neighbors.get(dir);
				IEnergyStorage handler = neighbor.getNullable();
				if (handler != null) {
					this.connections = (byte)(this.connections | mask);
				}
//				BlockEntity be = this.level.getBlockEntity(this.getBlockPos().relative(dir));
//				if(be instanceof IMultiblockBE<?> mbe) {
//					IMultiblockBEHelper<?> helper = mbe.getHelper();
//					IMultiblockContext<?> context = helper.getContext();
//					if(context == null) return oldConn != this.connections;
//					CapabilityPosition capPos = new CapabilityPosition(helper.getPositionInMB().relative(dir.getOpposite()), RelativeBlockFace.from(context.getLevel().getOrientation(),dir.getAxis().equals(Axis.Y) ? dir.getOpposite() : dir));
//					if(helper.getMultiblock().logic().getCapability((IMultiblockContext)context, capPos, ForgeCapabilities.ENERGY).isPresent())
//					{
//						this.connections = (byte)(this.connections | mask);
//					}
//				}
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
				BlockEntity be = this.level.getBlockEntity(this.getBlockPos().relative(dir));
				if (be instanceof IGEnergyPipeEntity) {
					availableConnections = (byte)(availableConnections | mask);
				} else {
					// Match updateConnectionByte: an exposed energy capability is enough. Testing
					// getEnergyStored() > 0 here made an idle machine render as unconnectable.
					IEnergyStorage handler = this.neighbors.get(dir).getNullable();
					if (handler != null) {
						availableConnections = (byte)(availableConnections | mask);
						continue;
					}
//					if(be instanceof IMultiblockBE<?> mbe) {
//						IMultiblockBEHelper<?> helper = mbe.getHelper();
//						IMultiblockContext<?> context = helper.getContext();
//						if(context == null) continue;
//						CapabilityPosition capPos = new CapabilityPosition(helper.getPositionInMB().relative(dir.getOpposite()), RelativeBlockFace.from(context.getLevel().getOrientation(),dir.getAxis().equals(Axis.Y) ? dir.getOpposite() : dir));
//						if(helper.getMultiblock().logic().getCapability((IMultiblockContext)context, capPos, ForgeCapabilities.ENERGY).isPresent())
//						{
//							availableConnections = (byte)(availableConnections | mask);
//						}
//					}
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
			IEnergyStorage handler = this.neighbors.get(connection).getNullable();
			BlockEntity con = Utils.getExistingTileEntity(this.level, this.getBlockPos().relative(connection));
			// A machine on this side gets a plug, another pipe gets a flange. The old form was
			// "handler.getEnergyStored() >= 0 &! (con instanceof ...)" - a dead test and a
			// non-short-circuiting '&' where '&&' was meant.
			if(handler != null && !(con instanceof IGEnergyPipeEntity))
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
				IEnergyStorage handler = this.neighbors.get(connection).getNullable();
				if(handler != null)
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
		/** Fallback throughput for a handler whose own block somehow isn't an {@link IGEnergyPipe}. */
		private static final int DEFAULT_TRANSFER_LIMIT = 32768;
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
			if (outputList.isEmpty()) {
				return 0;
			}

			BlockPos sourcePos = this.pipe.getBlockPos().relative(this.facing);
			int budget = Math.min(maxReceive, getTransferLimit());

			// First pass: how much is each output willing to take? Insertion-ordered so the second
			// pass walks the outputs in the same order it measured them.
			LinkedHashMap<IEnergyStorage, Integer> demands = new LinkedHashMap<>();
			long totalDemand = 0;
			for (DirectionalEnergyOutput output : outputList) {
				IEnergyStorage handler = getReceiver(output, sourcePos);
				if (handler == null) {
					continue;
				}

				int accepted = handler.receiveEnergy(budget, true);
				if (accepted > 0) {
					demands.put(handler, accepted);
					totalDemand += accepted;
				}
			}

			if (demands.isEmpty()) {
				return 0;
			}

			// Second pass: hand out the budget. "remaining" is the single source of truth for what is
			// left to give - deriving it from two counters is what used to produce negative shares,
			// and Forge's EnergyStorage applies a negative receiveEnergy as "energy += negative".
			int remaining = budget;
			for (Map.Entry<IEnergyStorage, Integer> entry : demands.entrySet()) {
				if (remaining <= 0) {
					break;
				}

				int demand = entry.getValue();
				int share = demand;
				if (totalDemand > budget) {
					// Proportional split, rounded up so rounding never strands part of the budget.
					long proportional = ((long)demand * budget + totalDemand - 1) / totalDemand;
					share = (int)Math.min(demand, Math.max(1L, proportional));
				}
				share = Math.min(share, remaining);

				int transferred = entry.getKey().receiveEnergy(share, simulate);
				if (transferred > 0) {
					remaining -= transferred;
				}
			}

			return budget - remaining;
		}

		/**
		 * The receiving handler behind {@code output}, or null if it should be skipped: stale, out of
		 * world, the neighbour that just pushed into this face, or unable to accept energy at all.
		 */
		@Nullable
		private IEnergyStorage getReceiver(DirectionalEnergyOutput output, BlockPos sourcePos) {
			BlockEntity tile = output.containingTile();
			BlockPos outputPos = tile.getBlockPos();
			if (outputPos.equals(sourcePos) || this.pipe.equals(tile) || !output.isValid()
					|| !this.pipe.level.hasChunkAt(outputPos) || !output.limitVoltage()) {
				return null;
			}

			IEnergyStorage handler = output.output();
			return handler != null && handler.canReceive() ? handler : null;
		}

		/**
		 * Throughput of the cable this handler belongs to. This used to be read off the <em>target</em>
		 * block, which is never a pipe, so the cable's own limit never applied.
		 */
		private int getTransferLimit() {
			Block self = this.pipe.getBlockState().getBlock();
			if (self instanceof IGEnergyPipe cable) return this.pipe.getTransferableAmount(cable);
			return DEFAULT_TRANSFER_LIMIT;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (maxExtract <= 0) {
				return 0;
			}

			Level world = this.pipe.getLevelNonnull();
			List<DirectionalEnergyOutput> sources = new ArrayList<>(
					IGEnergyPipeEntity.getConnectedEnergyHandlers(this.pipe.getBlockPos(), world));
			BlockPos sinkPos = this.pipe.getBlockPos().relative(this.facing);

			// Remove the block we're extracting to, plus anything stale or not actually a source
			sources.removeIf(output -> sinkPos.equals(output.containingTile().getBlockPos())
					|| !output.isValid()
					|| !output.output().canExtract());

			if (sources.isEmpty()) {
				return 0;
			}

			int budget = Math.min(maxExtract, getTransferLimit());

			// Start somewhere different each tick so one source isn't always drained first, but walk
			// the rest of the list too rather than giving up when the first pick happens to be empty.
			CURRENT_TICK_RANDOM.setSeed(HashCommon.mix(world.getGameTime()));
			int start = sources.size() == 1 ? 0 : CURRENT_TICK_RANDOM.nextInt(sources.size());

			int extracted = 0;
			for (int i = 0; i < sources.size() && extracted < budget; i++) {
				IEnergyStorage source = sources.get((start + i) % sources.size()).output();
				if (source == null) {
					continue;
				}
				extracted += Math.max(0, source.extractEnergy(budget - extracted, simulate));
			}

			return extracted;
		}

		@Override
		public int getEnergyStored() {
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

	public static record DirectionalEnergyOutput(LazyOptional<IEnergyStorage> reference, Direction direction, BlockEntity containingTile) {
		public DirectionalEnergyOutput(LazyOptional<IEnergyStorage> reference, Direction direction, BlockEntity containingTile) {
			this.reference = reference;
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

		/**
		 * False once the block entity is gone or the capability behind it has been invalidated, which
		 * is the point at which anything cached for this output has to be thrown away.
		 */
		public boolean isValid() {
			return !this.containingTile.isRemoved() && this.reference.isPresent();
		}

		@Nullable
		public IEnergyStorage output() {
			return this.reference.orElse(null);
		}

		public Direction direction() {
			return this.direction;
		}

		public BlockEntity containingTile() {
			return this.containingTile;
		}
	}
}