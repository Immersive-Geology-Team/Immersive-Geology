/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.helper.BeltRun;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;
import com.igteam.immersivegeology.common.block.belt.helper.BeltView;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.shape.*;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.core.Direction;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver.RiserResolution;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IGRisingBeltBlock extends IGBeltBlock
{
	public static final EnumProperty<RiserShape> RISER_SHAPE = EnumProperty.create("shape", RiserShape.class);
	public static final BooleanProperty OUTLET = BooleanProperty.create("outlet");

	public IGRisingBeltBlock(BeltStyle style)
	{
		super(style);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, RISER_SHAPE, RUN, OUTLET);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state.setValue(RISER_SHAPE, RiserShape.BASE).setValue(RUN, BeltRun.SINGLE).setValue(OUTLET, false);
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		return BeltShapes.riser(state.getValue(RISER_SHAPE), style().covered(), state.getValue(FACING));
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		return BeltNode.riser(state.getValue(FACING), style().covered());
	}

	@Override
	public String modelPiece(BlockState state)
	{
		return BeltModels.riserPiece(style(), state.getValue(RISER_SHAPE), state.getValue(RUN), state.getValue(OUTLET));
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.riserPiece(style(), RiserShape.BASE, BeltRun.SINGLE, false);
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		RiserResolution resolution = BeltShapeResolver.resolveRiser(view, pos);
		return state.setValue(RISER_SHAPE, resolution.shape()).setValue(RUN, resolution.run()).setValue(OUTLET, resolution.outlet());
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
		BlockState resolved = resolve(view, pos, state);
		if(resolved!=state)
			level.setBlock(pos, resolved, Block.UPDATE_CLIENTS);
	}

	@Override
	public boolean extendsLine()
	{
		return false;
	}

	@Override
	public BeltShape pathShape(BlockState state)
	{
		return BeltShape.STRAIGHT;
	}

	@Override
	public LanePath lanePath(BlockState state, Lane lane, boolean sideLoad)
	{
		return BeltPaths.forRiser(state.getValue(RISER_SHAPE), state.getValue(OUTLET), lane);
	}

	@Override
	public boolean acceptsItemsFrom(BlockState state, Direction side)
	{
		RiserShape shape = state.getValue(RISER_SHAPE);
		if(shape==RiserShape.STRAIGHT)
			return false;
		Direction facing = state.getValue(FACING);
		if(side==facing.getOpposite())
			return true;
		boolean left = shape==RiserShape.FROM_LEFT||shape==RiserShape.FROM_BOTH;
		boolean right = shape==RiserShape.FROM_RIGHT||shape==RiserShape.FROM_BOTH;
		return side==facing.getCounterClockWise()?left: side==facing.getClockWise()&&right;
	}

	@Override
	public boolean takesBothSides(BlockState state)
	{
		return false;
	}
}
