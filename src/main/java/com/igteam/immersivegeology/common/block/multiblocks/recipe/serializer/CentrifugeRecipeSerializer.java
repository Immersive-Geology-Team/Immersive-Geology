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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CentrifugeRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;

public class CentrifugeRecipeSerializer extends IERecipeSerializer<CentrifugeRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("fluid_input"), r -> r.fluidIn,
			TagOutput.CODECS.fieldOf("item_output"), r -> r.itemOutput,
			IEDualCodecs.FLUID_STACK.fieldOf("primary_fluid_out"), r -> r.primaryFluidOutput.get(),
			IEDualCodecs.FLUID_STACK.fieldOf("secondary_fluid_out"), r -> r.secondaryFluidOutput.get(),
			DualCodecs.INT.fieldOf("energy"), CentrifugeRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), CentrifugeRecipe::getTotalProcessTime,
			(input, output, primaryFluidOutput, secondaryFluidOutput, energy, time) ->
					new CentrifugeRecipe(input, output, Lazy.of(() -> primaryFluidOutput), Lazy.of(() -> secondaryFluidOutput), energy, time)
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.CENTRIFUGE.iconStack();
	}
}
