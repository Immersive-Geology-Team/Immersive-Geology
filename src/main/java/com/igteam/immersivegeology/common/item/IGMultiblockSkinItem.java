/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.IGChemicalReactorMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.part.SkinableMultiblockPart;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGChemicalReactorSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IGMultiblockSkinItem<T extends Enum<T> & IIGMultiSkinHelper & StringRepresentable> extends IGGenericItem
{
	private final T skin;
	private final String registryName;
	public IGMultiblockSkinItem(ItemCategoryFlags flag, MaterialInterface<?> material, T skinEnum, String registryName)
	{
		super(flag, material, new Properties().stacksTo(1));
		this.skin = skinEnum;
		this.registryName = registryName;
	}

	public Enum<?> getSkin()
	{
		return skin;
	}

	@Override
	public @NotNull Component getName(ItemStack stack)
	{
		return Component.translatable("item.immersivegeology." + category.getName(), Component.translatable("block.immersivegeology."+skin.multiblockName()));
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
	{
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

		Component skin_name = Component.translatable("skin.immersivegeology."+skin.multiblockName()+"."+skin.getSerializedName()).withStyle(skin.getColor());
		Component skin_type = Component.translatable("skin.immersivegeology.credit."+skin.getType().name().toLowerCase()).withStyle(skin.getType().getColor());
		Component credit = Component.translatable("skin.immersivegeology."+skin.getCredit()).withStyle(ChatFormatting.DARK_GRAY);
		Component description = Component.translatable("skin.immersivegeology.description", credit, skin_name, skin_type).withStyle(ChatFormatting.DARK_GRAY);

		pTooltipComponents.add(description);
	}

	public String getRegistryName()
	{
		return registryName;
	}
}
