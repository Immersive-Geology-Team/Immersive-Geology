/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.item;

import com.igteam.immersivegeology.common.block.belt.IGEndergateBlock;
import com.igteam.immersivegeology.common.block.belt.helper.BeltCoverHandler;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateLinks;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateRole;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class IGEndergateItem extends BlockItem
{
	public IGEndergateItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	public @NotNull InteractionResult place(@NotNull BlockPlaceContext context)
	{
		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		InteractionResult result = super.place(context);
		if(!result.consumesAction()||stack.isEmpty())
			return result;
		if(context.getPlayer()!=null)
			BeltCoverHandler.coverPlaced(context.getLevel(), pos, context.getPlayer(), getBlock());
		BlockState placed = context.getLevel().getBlockState(pos);
		if(!(placed.getBlock() instanceof IGEndergateBlock))
			return result;
		if(placed.getValue(IGEndergateBlock.ROLE)==EndergateRole.ENTRY)
			EndergateLinks.remember(stack, context.getLevel(), pos, placed.getValue(IGEndergateBlock.FACING));
		else
			EndergateLinks.forget(stack);
		return result;
	}
}
