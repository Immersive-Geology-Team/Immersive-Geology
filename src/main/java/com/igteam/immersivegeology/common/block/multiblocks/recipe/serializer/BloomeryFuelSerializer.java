/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BloomeryFuelSerializer extends IERecipeSerializer<BloomeryFuel>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, BloomeryFuel> CODECS = DualCompositeMapCodecs.composite(
			DualCodecs.INGREDIENT.fieldOf("input"), r -> r.input,
			DualCodecs.INT.optionalFieldOf("time", 1200), r -> r.burnTime,
			BloomeryFuel::new
	);

	public BloomeryFuelSerializer() {
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, BloomeryFuel> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Ingredients.COAL_COKE);
	}
}
