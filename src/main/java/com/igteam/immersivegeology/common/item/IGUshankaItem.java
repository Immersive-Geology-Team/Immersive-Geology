/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class IGUshankaItem extends ArmorItem
{
	private static final String TEXTURE = IGLib.MODID+":textures/models/armor_russian.png";

	public IGUshankaItem()
	{
		super(USHANKA_MATERIAL, Type.HELMET, new Properties().durability(77));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip,
								@NotNull TooltipFlag flag)
	{
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("item.immersivegeology.armor_russian_helmet.lore")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	@Nullable
	@Override
	public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel)
	{
		return ResourceLocation.parse(TEXTURE);
	}
}
