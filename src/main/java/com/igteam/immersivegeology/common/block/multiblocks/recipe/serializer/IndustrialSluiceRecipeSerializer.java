/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IndustrialSluiceRecipeSerializer extends IERecipeSerializer<IndustrialSluiceRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, IndustrialSluiceRecipe> CODECS = DualCompositeMapCodecs.composite(
			DualCodecs.INGREDIENT.fieldOf("input"), r -> r.itemIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			CHANCE_LIST_CODECS.fieldOf("byproducts"), IndustrialSluiceRecipe::getByproducts,
			DualCodecs.INT.fieldOf("water"), IndustrialSluiceRecipe::getTotalProcessWater,
			DualCodecs.INT.fieldOf("time"), IndustrialSluiceRecipe::getTotalProcessTime,
			DualCodecs.INT.fieldOf("energy"), IndustrialSluiceRecipe::getConfiguredEnergy,
			(input, output, byproducts, water, time, energy) -> {
				NonNullList<StackWithChance> byproductsList = NonNullList.createWithCapacity(byproducts.size());
				byproductsList.addAll(byproducts);
				return new IndustrialSluiceRecipe(input, output, byproductsList, water, time, energy);
			}
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, IndustrialSluiceRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return new ItemStack(Items.APPLE);
	}
}
