/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

public class GeothermalExchangerRecipeSerializer extends IERecipeSerializer<GeothermalExchangerRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, GeothermalExchangerRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), r -> r.fluidIn,
			IEDualCodecs.FLUID_STACK.fieldOf("fluidResult"), r -> r.fluidOutput.get(),
			DualCodecs.INT.fieldOf("energy"), GeothermalExchangerRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), GeothermalExchangerRecipe::getTotalProcessTime,
			(input, fluidOutput, energy, time) ->
					new GeothermalExchangerRecipe(input, Lazy.of(() -> fluidOutput), energy, time)
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, GeothermalExchangerRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}
}
