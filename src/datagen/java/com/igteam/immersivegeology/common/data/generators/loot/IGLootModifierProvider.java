/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.loot;

import com.igteam.immersivegeology.common.block.multiblocks.skins.*;
import com.igteam.immersivegeology.common.event.IGCommonForgeEvents;
import com.igteam.immersivegeology.common.loot.IGLootModifier;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.List;

public class IGLootModifierProvider extends GlobalLootModifierProvider
{

	public IGLootModifierProvider(PackOutput output)
	{
		super(output, IGLib.MODID);
	}

	@Override
	protected void start()
	{
		add("shipwreck_treasure_loot", new IGLootModifier(new LootItemCondition[]{
				LootTableIdCondition.builder(new ResourceLocation("chests/shipwreck_treasure")).build(),
				LootItemRandomChanceCondition.randomChance(0.5f).build()
		}, List.of(
				IGChemicalReactorSkins.RUSTED.getItem(),
				IGCrystallizerSkins.RUSTED.getItem()
		)));

		add("simple_dungeon_loot", new IGLootModifier(new LootItemCondition[]{
				LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build(),
				LootItemRandomChanceCondition.randomChance(0.5f).build()
		}, List.of(
				IGChemicalReactorSkins.LEGACY.getItem(),
				IGRevFurnaceSkins.LEGACY.getItem(),
				IGGravitySeparatorSkins.LEGACY.getItem(),
				IGRotaryKilnSkins.LEGACY.getItem(),
				IGPelletizerSkins.LEGACY.getItem()
		)));

		add("desert_pyramid_loot", new IGLootModifier(new LootItemCondition[]{
				LootTableIdCondition.builder(new ResourceLocation("chests/desert_pyramid")).build(),
				LootItemRandomChanceCondition.randomChance(0.5f).build()
		}, List.of(
				IGChemicalReactorSkins.HAZARD.getItem(),
				IGGravitySeparatorSkins.HAZARD.getItem()
		)));

		add("woodland_mansion_loot", new IGLootModifier(new LootItemCondition[]{
				LootTableIdCondition.builder(new ResourceLocation("chests/woodland_mansion")).build(),
				LootItemRandomChanceCondition.randomChance(0.5f).build()
		}, List.of(
				IGGravitySeparatorSkins.GREEN.getItem()
		)));
	}
}
