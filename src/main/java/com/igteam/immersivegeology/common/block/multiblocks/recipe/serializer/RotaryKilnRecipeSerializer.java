/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.serialization.DataResult;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class RotaryKilnRecipeSerializer extends IERecipeSerializer<RotaryKilnRecipe>
{
	private static final DualMapCodec<RegistryFriendlyByteBuf, RotaryKilnRecipe> BASE_CODECS = DualCompositeMapCodecs.composite(
			IngredientWithSize.CODECS.fieldOf("input"), r -> r.itemIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			DualCodecs.INT.fieldOf("time"), RotaryKilnRecipe::getTotalProcessTime,
			DualCodecs.INT.fieldOf("heat"), RotaryKilnRecipe::getHeatRequired,
			RotaryKilnRecipe::new
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, RotaryKilnRecipe> CODECS = new DualMapCodec<>(
			BASE_CODECS.mapCodec().validate(RotaryKilnRecipeSerializer::validate),
			BASE_CODECS.streamCodec()
	);

	/**
	 * A time of zero divides by zero when Immersive Engineering works out the per-tick energy,
	 * and a heat the kiln cannot physically reach would leave it heating forever, so both are
	 * rejected at load rather than at runtime.
	 */
	private static DataResult<RotaryKilnRecipe> validate(RotaryKilnRecipe recipe)
	{
		int time = recipe.getTotalProcessTime();
		int heat = recipe.getHeatRequired();
		if(time <= 0)
			return DataResult.error(() -> "Rotary Kiln recipe has a time of "+time+", it must be at least 1 tick");
		if(heat < 0||heat > RotaryKilnLogic.EHV_HEAT_CAP)
			return DataResult.error(() -> "Rotary Kiln recipe requires a heat of "+heat+", it must be between 0 and "+RotaryKilnLogic.EHV_HEAT_CAP);
		return DataResult.success(recipe);
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, RotaryKilnRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.ROTARYKILN.iconStack();
	}
}
