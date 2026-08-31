/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;

public class IGItemPellet extends IGGenericItem
{
	public IGItemPellet(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
	{
		MaterialInterface<?> material = getMaterial(MaterialTexture.base).getPrimaryProduct();
		pTooltipComponents.add(Component.translatable("material.immersivegeology." + material.getName()).withStyle(ChatFormatting.GOLD));

		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
}
