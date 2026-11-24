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
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IGExtractionBeltBlock extends IGBeltBlock
{
	public IGExtractionBeltBlock(BeltStyle style)
	{
		super(style);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state;
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		return BeltShapes.extraction(style().covered(), state.getValue(FACING));
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		return BeltNode.extraction(state.getValue(FACING));
	}

	@Override
	public String modelPiece(BlockState state)
	{
		return BeltModels.extractionPiece(style());
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.extractionPiece(style());
	}

	public static BlockPos sourcePos(BlockState state, BlockPos pos)
	{
		return pos.relative(state.getValue(FACING).getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		Direction clicked = context.getClickedFace();
		Direction facing = clicked.getAxis().isHorizontal()?clicked: context.getHorizontalDirection();
		return defaultBlockState().setValue(FACING, facing);
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		return state;
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
	}

	@Override
	public boolean extendsLine()
	{
		return false;
	}

	private static final int EXTRACT_INTERVAL = 8;

	@Override
	public BeltShape pathShape(BlockState state)
	{
		return BeltShape.STRAIGHT;
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt)
	{
		if(level.getGameTime()%EXTRACT_INTERVAL!=0)
			return;
		BlockEntity source = level.getBlockEntity(sourcePos(state, pos));
		if(source==null)
			return;
		IItemHandler handler = source.getCapability(ForgeCapabilities.ITEM_HANDLER, state.getValue(FACING)).orElse(null);
		if(handler==null)
			return;
		for(int slot = 0; slot < handler.getSlots(); slot++)
		{
			ItemStack available = handler.extractItem(slot, handler.getSlotLimit(slot), true);
			if(available.isEmpty())
				continue;
			ItemStack refused = belt.insertAnyLane(this, state, 0, available, true);
			int accepted = available.getCount()-refused.getCount();
			if(accepted <= 0)
				return;
			belt.insertAnyLane(this, state, 0, handler.extractItem(slot, accepted, false), false);
			return;
		}
	}
}
