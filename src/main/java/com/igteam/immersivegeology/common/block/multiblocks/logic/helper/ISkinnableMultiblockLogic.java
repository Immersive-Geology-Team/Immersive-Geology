/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.IGGravitySeparatorMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GravitySeparatorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.part.SkinableMultiblockPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGGravitySeparatorSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.common.item.IGMultiblockSkinItem;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ISkinnableMultiblockLogic<State extends IMultiblockState> extends IMultiblockLogic<State>
{
	default InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
	{
		ItemStack stack = player.getItemInHand(hand);
		Level level = ctx.getLevel().getRawLevel();
		BlockState state = ctx.getLevel().getBlockState(posInMultiblock);
		if(stack.getItem() instanceof IGMultiblockSkinItem<?> skin && skin.getSkin() instanceof IIGMultiSkinHelper)
		{
			String t = state.getBlock().getDescriptionId();
			String s = t.substring(t.lastIndexOf(".")+1);
			if(((IIGMultiSkinHelper)skin.getSkin()).multiblockName().contains(s))
			{
				boolean success = SkinableMultiblockPart.setSkin(ctx.getLevel(), (IGTemplateMultiblock) IGRegistrationHolder.getMBTemplate.apply(((IIGMultiSkinHelper)skin.getSkin()).multiblockName()), skin.getSkin());
				if(success)
				{
					return InteractionResult.SUCCESS;
				}
			}
		}

		return InteractionResult.FAIL;
	}
}
