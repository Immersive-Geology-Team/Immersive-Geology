/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryAlloyRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.serialization.DataResult;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class FoundryAlloyRecipeSerializer extends IERecipeSerializer<FoundryAlloyRecipe>
{
	private static final DualMapCodec<RegistryFriendlyByteBuf, FoundryAlloyRecipe> BASE_CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.listOf().fieldOf("inputs"), r -> r.alloyInputs,
			IEDualCodecs.FLUID_STACK.fieldOf("result"), r -> r.alloyOutput,
			DualCodecs.INT.fieldOf("energy"), FoundryAlloyRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), FoundryAlloyRecipe::getTotalProcessTime,
			FoundryAlloyRecipe::new
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, FoundryAlloyRecipe> CODECS = new DualMapCodec<>(
			BASE_CODECS.mapCodec().validate(FoundryAlloyRecipeSerializer::validate),
			BASE_CODECS.streamCodec()
	);

	private static DataResult<FoundryAlloyRecipe> validate(FoundryAlloyRecipe recipe)
	{
		if(recipe.alloyInputs.size() < 2)
			return DataResult.error(() -> "Foundry alloying recipe needs at least two fluid inputs to merge");
		for(SizedFluidIngredient input : recipe.alloyInputs)
			if(input.amount() <= 0)
				return DataResult.error(() -> "Foundry alloying recipe has an input part of zero, every part of the ratio must be above zero");
		if(recipe.alloyOutput.isEmpty() || recipe.alloyOutput.getAmount() <= 0)
			return DataResult.error(() -> "Foundry alloying recipe produces no alloy");
		if(recipe.getTotalProcessEnergy() < 0)
			return DataResult.error(() -> "Foundry alloying recipe has a negative energy cost");
		if(recipe.getTotalProcessTime() <= 0)
			return DataResult.error(() -> "Foundry alloying recipe must have a time of at least 1 tick");
		return DataResult.success(recipe);
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, FoundryAlloyRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.FOUNDRY.iconStack();
	}
}
