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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class RevFurnaceRecipeSerializer extends IERecipeSerializer<RevFurnaceRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, RevFurnaceRecipe> CODECS = DualCompositeMapCodecs.composite(
			IngredientWithSize.CODECS.fieldOf("input"), r -> r.input,
			TagOutput.CODECS.fieldOf("result"), r -> r.result,
			DualCodecs.INT.fieldOf("waste"), RevFurnaceRecipe::getWasteAmount,
			DualCodecs.INT.fieldOf("time"), RevFurnaceRecipe::getTotalProcessTime,
			RevFurnaceRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, RevFurnaceRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.REVERBERATION_FURNACE.iconStack();
	}
}
