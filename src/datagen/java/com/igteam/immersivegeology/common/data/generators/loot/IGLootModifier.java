/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.loot;

import com.igteam.immersivegeology.common.block.multiblocks.skins.IGChemicalReactorSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGRotaryKilnSkins;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class IGLootModifier extends LootModifier
{
	public static final Codec<IGLootModifier> CODEC = RecordCodecBuilder.create(inst ->
			// codecStart handles the "conditions" array automatically
			LootModifier.codecStart(inst)
					.apply(inst, IGLootModifier::new)
	);

	protected IGLootModifier(LootItemCondition[] conditionsIn)
	{
		super(conditionsIn);
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		generatedLoot.add(new ItemStack(IGChemicalReactorSkins.HAZARD.getItem()));
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
}
