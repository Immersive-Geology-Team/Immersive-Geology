/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.entity.IGPoweredBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;
import com.igteam.immersivegeology.common.block.belt.helper.BeltView;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.shape.BeltModels;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShape;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver.Resolution;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IGPoweredBeltBlock extends IGBeltBlock
{
	public static final EnumProperty<BeltShape> POWERED_SHAPE = EnumProperty.create("shape", BeltShape.class, BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	private static final double POST_X = 3.5;
	private static final double POST_Z = 0.5;
	private static final double POST_TOP = 15;
	private static final double PLATE_Y = 8;

	private final Map<BlockState, VoxelShape> collisions = new HashMap<>();
	private final Map<BlockState, VoxelShape> supportShapes = new HashMap<>();

	public IGPoweredBeltBlock(BeltStyle style)
	{
		super(style);
		for(BlockState state : getStateDefinition().getPossibleStates())
		{
			collisions.put(state, BeltShapes.poweredCollision(state.getValue(POWERED_SHAPE), style.covered(), state.getValue(FACING)));
			supportShapes.put(state, leverSides(state).stream().map(BeltShapes::switchFace).reduce(Shapes.empty(), Shapes::or));
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED_SHAPE, POWERED);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state.setValue(POWERED_SHAPE, BeltShape.STRAIGHT).setValue(POWERED, false);
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		return BeltShapes.poweredSelection(state.getValue(POWERED_SHAPE), style().covered(), state.getValue(FACING));
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return collisions.get(state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		return supportShapes.get(state);
	}

	public static List<Direction> leverSides(BlockState state)
	{
		Direction facing = state.getValue(FACING);
		return switch(state.getValue(POWERED_SHAPE))
		{
			case FROM_LEFT -> List.of(facing.getClockWise());
			case FROM_RIGHT -> List.of(facing.getCounterClockWise());
			default -> List.of(facing.getCounterClockWise(), facing.getClockWise());
		};
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		return BeltNode.fixed(state.getValue(FACING));
	}

	@Override
	public String modelPiece(BlockState state)
	{
		return BeltModels.poweredPiece(style(), state.getValue(POWERED_SHAPE));
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.poweredPiece(style(), BeltShape.STRAIGHT);
	}

	@Override
	public boolean stoppedBelt(BlockState state)
	{
		return state.getValue(POWERED);
	}

	public static Direction switchSide(BlockState state)
	{
		Direction facing = state.getValue(FACING);
		return state.getValue(POWERED_SHAPE)==BeltShape.FROM_LEFT?facing.getClockWise(): facing.getCounterClockWise();
	}

	public Vec3 wirePoint(BlockState state)
	{
		boolean right = state.getValue(POWERED_SHAPE)==BeltShape.FROM_LEFT;
		double x = style().covered()?8: POST_X;
		double y = style().covered()?PLATE_Y: POST_TOP;
		double z = style().covered()?0: POST_Z;
		Vec3 canonical = new Vec3(right?x: -x, y, z);
		Vec3 turned = BeltShapes.turnPoint(canonical, state.getValue(FACING));
		return new Vec3((turned.x+8)/16, turned.y/16, (turned.z+8)/16);
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		Resolution resolution = BeltShapeResolver.resolve(view, pos);
		return state.setValue(POWERED_SHAPE, BeltShapeResolver.poweredShape(resolution.shape(), state.getValue(POWERED_SHAPE)));
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
		BlockState resolved = resolve(view, pos, state);
		if(resolved==state)
			return;
		boolean switchMoved = switchSide(resolved)!=switchSide(state);
		level.setBlock(pos, resolved, Block.UPDATE_ALL);
		if(switchMoved&&level.getBlockEntity(pos) instanceof IGPoweredBeltBlockEntity belt)
			belt.onSwitchMoved();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState placed = super.getStateForPlacement(context);
		return placed==null?null: placed.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean moving)
	{
		super.neighborChanged(state, level, pos, block, fromPos, moving);
		if(level.isClientSide)
			return;
		if(level.getBlockEntity(pos) instanceof IGPoweredBeltBlockEntity belt)
			belt.markInputDirty();
		updatePowered(level, pos);
	}

	public void updatePowered(Level level, BlockPos pos)
	{
		BlockState state = level.getBlockState(pos);
		if(!state.is(this))
			return;
		int wire = level.getBlockEntity(pos) instanceof IGPoweredBeltBlockEntity belt?belt.wireSignal(): 0;
		boolean powered = level.hasNeighborSignal(pos)||wire > 0;
		if(state.getValue(POWERED)!=powered)
			level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit)
	{
		ItemStack held = player.getItemInHand(hand);
		if(!(held.getItem() instanceof DyeItem dye))
			return super.use(state, level, pos, player, hand, hit);
		if(!level.isClientSide&&level.getBlockEntity(pos) instanceof IGPoweredBeltBlockEntity belt)
		{
			belt.setChannel(dye.getDyeColor());
			player.displayClientMessage(Component.translatable("block.immersivegeology.powered_belt.channel",
					Component.translatable("color.minecraft."+dye.getDyeColor().getName())), true);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new IGPoweredBeltBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		if(type!=IGRegistrationHolder.POWERED_BELT.get())
			return null;
		return (lvl, pos, st, be) -> {
			IGPoweredBeltBlockEntity belt = (IGPoweredBeltBlockEntity)be;
			BeltTransport.tick(lvl, pos, st, belt);
			if(!lvl.isClientSide)
				IGPoweredBeltBlockEntity.serverTick(lvl, pos, st, belt);
		};
	}

	@Override
	public BeltShape pathShape(BlockState state)
	{
		return state.getValue(POWERED_SHAPE);
	}

	@Override
	public boolean isMoving(BlockState state)
	{
		return !state.getValue(POWERED);
	}
}
