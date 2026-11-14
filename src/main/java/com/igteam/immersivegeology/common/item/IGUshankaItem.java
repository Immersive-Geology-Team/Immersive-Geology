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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class IGUshankaItem extends ArmorItem
{
	private static final String TEXTURE = IGLib.MODID+":textures/models/armor_russian.png";

	private static final DeferredRegister<ArmorMaterial> REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, IGLib.MODID);

	public static final Holder<ArmorMaterial> USHANKA_MATERIAL = REGISTER.register("ushanka", () -> new ArmorMaterial(
			Map.of(Type.HELMET, 2),
			ArmorMaterials.LEATHER.value().enchantmentValue(),
			SoundEvents.ARMOR_EQUIP_LEATHER,
			() -> Ingredient.of(Items.LEATHER),
			List.of(new Layer(IGLib.rl("ushanka"))),
			0,
			0
	));

	public IGUshankaItem()
	{
		super(USHANKA_MATERIAL, Type.HELMET, new Properties().durability(77));
	}

	public static void init(IEventBus eventBus)
	{
		REGISTER.register(eventBus);
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
