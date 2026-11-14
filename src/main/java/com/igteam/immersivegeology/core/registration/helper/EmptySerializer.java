/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration.helper;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.mojang.serialization.MapCodec;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class EmptySerializer extends IERecipeSerializer<EmptyRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, EmptyRecipe> CODECS = new DualMapCodec<>(
			MapCodec.unit(EmptyRecipe::new),
			StreamCodec.of((buffer, recipe) -> {}, buffer -> new EmptyRecipe())
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, EmptyRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return ItemStack.EMPTY;
	}
}
