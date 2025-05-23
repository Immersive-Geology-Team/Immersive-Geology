/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.loot;

import com.igteam.immersivegeology.common.block.multiblocks.skins.IGChemicalReactorSkins;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IGLootModifier extends LootModifier
{
	public static final Codec<IGLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).and(
			ForgeRegistries.ITEMS.getCodec().listOf().fieldOf("items").forGetter(m -> m.item_pool)
	).apply(inst, IGLootModifier::new));

	private final List<Item> item_pool;
	public IGLootModifier(LootItemCondition[] conditionsIn, List<Item> item_pool)
	{
		super(conditionsIn);
		this.item_pool = item_pool;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		if (!item_pool.isEmpty()) {
			// Randomly select one item from the list
			Item selectedItem = item_pool.get(context.getRandom().nextInt(item_pool.size()));

			// Add the selected item to the loot
			generatedLoot.add(new ItemStack(selectedItem));
		}

		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}
