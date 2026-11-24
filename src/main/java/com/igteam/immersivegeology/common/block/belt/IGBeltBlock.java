/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.*;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.network.IBeltConnectable;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.shape.*;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver.Resolution;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.block.belt.transport.LaneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class IGBeltBlock extends Block implements EntityBlock, IBeltConnectable
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<BeltShape> SHAPE = EnumProperty.create("shape", BeltShape.class);
	public static final EnumProperty<BeltRun> RUN = EnumProperty.create("run", BeltRun.class);
	public static final BooleanProperty CAPPED = BooleanProperty.create("capped");

	private static final double ENTITY_PUSH = BeltTransport.BLOCKS_PER_TICK*0.45;
	private static final double GRAB_RADIUS = 0.25;

	private final BeltStyle style;
	private final Map<BlockState, VoxelShape> shapes = new HashMap<>();

	public IGBeltBlock(BeltStyle style)
	{
		this(style, defaultProperties());
	}

	public IGBeltBlock(BeltStyle style, BlockBehaviour.Properties properties)
	{
		super(properties);
		this.style = style;
		registerDefaultState(initialState(getStateDefinition().any().setValue(FACING, Direction.NORTH)));
		for(BlockState state : getStateDefinition().getPossibleStates())
			shapes.put(state, computeShape(state));
	}

	protected BlockState initialState(BlockState state)
	{
		return state.setValue(SHAPE, BeltShape.STRAIGHT).setValue(RUN, BeltRun.SINGLE).setValue(CAPPED, false);
	}

	protected VoxelShape computeShape(BlockState state)
	{
		BeltShape shape = state.getValue(SHAPE);
		return BeltShapes.rotate(BeltShapes.canonical(shape, style.covered()), shape, state.getValue(FACING));
	}

	public String modelPiece(BlockState state)
	{
		return BeltModels.piece(style, state.getValue(SHAPE), state.getValue(RUN), state.getValue(CAPPED));
	}

	public BeltShape pathShape(BlockState state)
	{
		return state.getValue(SHAPE);
	}

	public LanePath lanePath(BlockState state, Lane lane, boolean sideLoad)
	{
		return BeltPaths.forShape(pathShape(state), lane, sideLoad);
	}

	public boolean acceptsItemsFrom(BlockState state, Direction side)
	{
		BeltNode node = beltNode(state);
		if(node==null)
			return false;
		if(side==node.facing().getOpposite())
			return node.acceptsBack();
		if(!node.acceptsSides())
			return false;
		BeltShape open = pathShape(state).sideInputs();
		if(open==BeltShape.FROM_BOTH)
			return true;
		return side==node.left()?open==BeltShape.FROM_LEFT: side==node.right()&&open==BeltShape.FROM_RIGHT;
	}

	public float sideEntry(BlockState state)
	{
		return BeltTransport.SIDE_ENTRY;
	}

	public boolean acceptsSideLane(BlockState state, Direction side, Direction laneSide)
	{
		return true;
	}

	public boolean takesBothSides(BlockState state)
	{
		return pathShape(state).sideInputs()==BeltShape.FROM_BOTH;
	}

	public ItemStack deliver(Level level, BlockPos pos, BlockState state, Lane lane, ItemStack stack, boolean simulate)
	{
		return BeltTransport.deliver(level, pos, state, this, lane, stack, simulate);
	}

		public boolean isMoving(BlockState state)
	{
		return true;
	}

	public float holdPoint(BlockState state, LaneItem item)
	{
		return 1;
	}

	public void serverTick(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt)
	{
	}

	public void onItemMoved(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt, Lane lane, LaneItem item, float before)
	{
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		return (lvl, pos, st, be) -> {
			if(be instanceof IGBeltBlockEntity belt)
				BeltTransport.tick(lvl, pos, st, belt);
		};
	}

	@Override
	public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity)
	{
		super.stepOn(level, pos, state, entity);
		if(!carriesEntities(state))
			return;
		if(entity instanceof ItemEntity item)
		{
			if(!level.isClientSide&&item.isAlive()&&level.getBlockEntity(pos) instanceof IGBeltBlockEntity belt)
				pickUp(state, pos, belt, item, state.getValue(FACING));
			return;
		}
		pushEntity(state, entity);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity entity)
	{
		return getShape(state, level, pos, CollisionContext.empty()).max(Direction.Axis.Y) > 1;
	}

	public boolean carriesEntities(BlockState state)
	{
		return !style.covered()&&isMoving(state);
	}

	public void pushEntity(BlockState state, Entity entity)
	{
		Direction facing = state.getValue(FACING);
		Vec3 motion = entity.getDeltaMovement();
		entity.setDeltaMovement(motion.x+facing.getStepX()*ENTITY_PUSH, motion.y, motion.z+facing.getStepZ()*ENTITY_PUSH);
	}

	private void pickUp(BlockState state, BlockPos pos, IGBeltBlockEntity belt, ItemEntity item, Direction facing)
	{
		Vec3 local = item.position().subtract(Vec3.atBottomCenterOf(pos));
		double across = local.x*facing.getCounterClockWise().getStepX()+local.z*facing.getCounterClockWise().getStepZ();
		double along = local.x*facing.getStepX()+local.z*facing.getStepZ();
		Lane lane = across >= 0?Lane.LEFT: Lane.RIGHT;
		float entry = (float)Math.max(0, Math.min(1, along+0.5));
		ItemStack left = belt.insert(lane, entry, item.getItem(), lanePath(state, lane, belt.isSideLoad()), false);
		if(left.isEmpty())
			item.discard();
		else
			item.setItem(left);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit)
	{
		ItemStack held = player.getItemInHand(hand);
		if(player.isSecondaryUseActive())
			return hand==InteractionHand.MAIN_HAND&&held.isEmpty()?BeltCoverHandler.uncover(level, pos, state, player): InteractionResult.PASS;
		if(held.getItem() instanceof BlockItem item&&item.getBlock() instanceof IGBeltBlock)
			return InteractionResult.PASS;
		return takeItem(level, pos, state, player);
	}

	protected InteractionResult takeItem(Level level, BlockPos pos, BlockState state, Player player)
	{
		if(!(level.getBlockEntity(pos) instanceof IGBeltBlockEntity belt)||belt.isEmpty())
			return InteractionResult.PASS;
		Vec3 eye = player.getEyePosition();
		Vec3 look = player.getViewVector(1);
		Direction facing = state.getValue(FACING);
		Lane grabbedLane = null;
		LaneItem grabbed = null;
		double best = GRAB_RADIUS;
		for(Lane lane : Lane.values())
		{
			LanePath path = lanePath(state, lane, belt.isSideLoad());
			for(LaneItem item : belt.lane(lane))
			{
				Vec3 local = BeltShapes.turnPoint(path.point(item.progress()), facing);
				Vec3 offset = new Vec3(pos.getX()+(local.x+8)/16, pos.getY()+local.y/16, pos.getZ()+(local.z+8)/16).subtract(eye);
				double along = offset.dot(look);
				double miss = offset.subtract(look.scale(along)).length();
				if(along > 0&&miss < best)
				{
					best = miss;
					grabbed = item;
					grabbedLane = lane;
				}
			}
		}
		if(grabbed==null)
			return InteractionResult.PASS;
		if(!level.isClientSide)
		{
			belt.lane(grabbedLane).remove(grabbed);
			belt.markLanesChanged();
			belt.flushSync();
			ItemHandlerHelper.giveItemToPlayer(player, grabbed.stack());
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	public boolean drawsItem(BlockState state, float progress)
	{
		return true;
	}

	public boolean extendsLine()
	{
		return true;
	}

	public boolean rendersModel(BlockState state)
	{
		return true;
	}

	public boolean stoppedBelt(BlockState state)
	{
		return false;
	}

	public String itemModelPiece()
	{
		return BeltModels.family(style)+"/straight";
	}

	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		return withResolution(state, BeltShapeResolver.resolve(view, pos));
	}

	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
		Resolution resolution = BeltShapeResolver.resolve(view, pos);
		BlockState resolved = withResolution(state, resolution);
		if(resolved!=state)
			level.setBlock(pos, resolved, Block.UPDATE_CLIENTS);
		if(level.getBlockEntity(pos) instanceof IGBeltBlockEntity belt)
			belt.setSideLoad(resolution.sideLoad());
	}

	public static BlockBehaviour.Properties defaultProperties()
	{
		return BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.sound(SoundType.METAL)
				.strength(3f, 6f)
				.noOcclusion()
				.isViewBlocking((state, level, pos) -> false)
				.pushReaction(PushReaction.BLOCK);
	}

	public BeltStyle style()
	{
		return style;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, SHAPE, RUN, CAPPED);
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		return BeltNode.basic(state.getValue(FACING), style.covered());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState placed = defaultBlockState().setValue(FACING, context.getHorizontalDirection());
		BlockPos pos = context.getClickedPos();
		return resolve(new LevelBeltView(context.getLevel(), pos, placed), pos, placed);
	}

	public static BlockState withResolution(BlockState state, Resolution resolution)
	{
		return state.setValue(SHAPE, resolution.shape()).setValue(RUN, resolution.run()).setValue(CAPPED, resolution.capped());
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean moving)
	{
		super.onPlace(state, level, pos, oldState, moving);
		if(!level.isClientSide&&factsChanged(oldState, state))
			refreshAround(level, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moving)
	{
		boolean replaced = !state.is(newState.getBlock());
		if(replaced&&!level.isClientSide&&level.getBlockEntity(pos) instanceof IGBeltBlockEntity belt)
			for(ItemStack stack : belt.takeContents())
				Containers.dropItemStack(level, pos.getX()+.5, pos.getY()+.25, pos.getZ()+.5, stack);
		super.onRemove(state, level, pos, newState, moving);
		if(!level.isClientSide&&replaced)
			refreshAround(level, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean moving)
	{
		super.neighborChanged(state, level, pos, block, fromPos, moving);
		if(!level.isClientSide&&fromPos.equals(pos.above()))
			refreshAround(level, pos);
	}

	private static boolean factsChanged(BlockState oldState, BlockState newState)
	{
		if(!oldState.is(newState.getBlock()))
			return true;
		return oldState.getValue(FACING)!=newState.getValue(FACING);
	}

	public static void refreshAround(Level level, BlockPos center)
	{
		LevelBeltView view = new LevelBeltView(level);
		int h = BeltShapeResolver.REACH_HORIZONTAL;
		int v = BeltShapeResolver.REACH_VERTICAL;
		for(BlockPos pos : BlockPos.betweenClosed(center.offset(-h, -v, -h), center.offset(h, v, h)))
		{
			BlockState state = level.getBlockState(pos);
			if(state.getBlock() instanceof IGBeltBlock belt)
				belt.refresh(level, pos.immutable(), state, view);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return shapes.getOrDefault(state, Shapes.block());
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new IGBeltBlockEntity(pos, state);
	}
}
