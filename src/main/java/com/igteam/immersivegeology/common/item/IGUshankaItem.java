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
		super(Material.INSTANCE, Type.HELMET, new Properties());
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
								@NotNull TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("item.immersivegeology.armor_russian_helmet.lore")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	@Nullable
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
	{
		return TEXTURE;
	}

	private enum Material implements ArmorMaterial
	{
		INSTANCE;

		private static final int DURABILITY = 77;
		private static final int DEFENCE = 2;

		@Override
		public int getDurabilityForType(@NotNull Type type)
		{
			return type==Type.HELMET?DURABILITY: 0;
		}

		@Override
		public int getDefenseForType(@NotNull Type type)
		{
			return type==Type.HELMET?DEFENCE: 0;
		}

		@Override
		public int getEnchantmentValue()
		{
			return ArmorMaterials.LEATHER.getEnchantmentValue();
		}

		@NotNull
		@Override
		public SoundEvent getEquipSound()
		{
			return SoundEvents.ARMOR_EQUIP_LEATHER;
		}

		@NotNull
		@Override
		public Ingredient getRepairIngredient()
		{
			return Ingredient.of(Items.LEATHER);
		}

		@NotNull
		@Override
		public String getName()
		{
			return IGLib.MODID+":ushanka";
		}

		@Override
		public float getToughness()
		{
			return 0;
		}

		@Override
		public float getKnockbackResistance()
		{
			return 0;
		}
	}
}
