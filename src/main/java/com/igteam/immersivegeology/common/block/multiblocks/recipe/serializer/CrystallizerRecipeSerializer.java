/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

public class CrystallizerRecipeSerializer extends IERecipeSerializer<CrystallizerRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), r -> r.fluidIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			IEDualCodecs.FLUID_STACK.fieldOf("fluidResult"), r -> r.fluidOutput.get(),
			DualCodecs.INT.fieldOf("energy"), CrystallizerRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), CrystallizerRecipe::getTotalProcessTime,
			(input, output, fluidOutput, energy, time) ->
					new CrystallizerRecipe(input, output, Lazy.of(() -> fluidOutput), energy, time)
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.CRYSTALLIZER.iconStack();
	}
}
