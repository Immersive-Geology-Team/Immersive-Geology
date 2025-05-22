/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.loot;

import blusunrize.immersiveengineering.api.IEApi;
import com.igteam.immersivegeology.common.block.multiblocks.skins.*;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class IGChestLootProvider implements LootTableSubProvider
{
	@Override
	public void generate(BiConsumer<ResourceLocation, Builder> out)
	{

	}

	private LootPoolEntryContainer.Builder<?> createEntry(ItemLike item)
	{
		return LootItem.lootTableItem(item);
	}

	private LootPoolEntryContainer.Builder<?> createEntry(ItemLike item, int weight, int min, int max)
	{
		return createEntry(new ItemStack(item), weight)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
	}

	private LootPoolSingletonContainer.Builder<?> createEntry(ItemStack item, int weight)
	{
		LootPoolSingletonContainer.Builder<?> ret = LootItem.lootTableItem(item.getItem())
				.setWeight(weight);
		if(item.hasTag())
			ret.apply(SetNbtFunction.setTag(item.getOrCreateTag()));
		return ret;
	}
}
