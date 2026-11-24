/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;
import com.igteam.immersivegeology.common.block.belt.helper.BeltView;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.shape.BeltModels;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShape;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.block.belt.transport.LaneItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IGDroppingBeltBlock extends IGBeltBlock
{
	public static final EnumProperty<BeltShape> DROPPING_SHAPE = EnumProperty.create("shape", BeltShape.class, BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT, BeltShape.FROM_BOTH);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final int DROP_RETRY_INTERVAL = 8;

	public IGDroppingBeltBlock(BeltStyle style)
	{
		super(style);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, DROPPING_SHAPE, POWERED);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state.setValue(DROPPING_SHAPE, BeltShape.STRAIGHT).setValue(POWERED, false);
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		BeltShape shape = state.getValue(DROPPING_SHAPE);
		return BeltShapes.rotate(BeltShapes.canonical(shape, style().covered()), shape, state.getValue(FACING));
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
		return BeltModels.droppingPiece(style(), state.getValue(DROPPING_SHAPE));
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.droppingPiece(style(), BeltShape.STRAIGHT);
	}

	public static boolean dropsItems(BlockState state)
	{
		return !state.getValue(POWERED);
	}

	public static BlockPos dropTarget(BlockPos pos)
	{
		return pos.below();
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		return state.setValue(DROPPING_SHAPE, BeltShapeResolver.resolve(view, pos).shape().sideInputs());
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
		BlockState resolved = resolve(view, pos, state);
		if(resolved!=state)
			level.setBlock(pos, resolved, Block.UPDATE_CLIENTS);
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
		BlockState current = level.getBlockState(pos);
		boolean powered = level.hasNeighborSignal(pos);
		if(current.is(this)&&current.getValue(POWERED)!=powered)
			level.setBlock(pos, current.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
	}

	@Override
	public BeltShape pathShape(BlockState state)
	{
		return state.getValue(DROPPING_SHAPE);
	}

	@Override
	public void onItemMoved(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt, Lane lane, LaneItem item, float before)
	{
		if(!dropsItems(state)||item.progress() < BeltTransport.SIDE_ENTRY)
			return;
		boolean crossing = before < BeltTransport.SIDE_ENTRY;
		boolean stalled = item.progress()==before;
		if(!crossing&&!(stalled&&level.getGameTime()%DROP_RETRY_INTERVAL==0))
			return;
		ItemStack left = BeltTransport.dropBelow(level, pos, item.stack());
		if(left.getCount()!=item.stack().getCount())
		{
			item.setStack(left);
			belt.markLanesChanged();
		}
	}
}
