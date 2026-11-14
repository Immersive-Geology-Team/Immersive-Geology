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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.FoundryRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodec;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FoundryRecipeSerializer extends IERecipeSerializer<FoundryRecipe>
{
	private static final DualCodec<RegistryFriendlyByteBuf, Item> MOLD_CODECS = new DualCodec<>(
			ResourceLocation.CODEC.xmap(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey),
			ByteBufCodecs.registry(Registries.ITEM)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, FoundryRecipe> CODECS = DualCompositeMapCodecs.composite(
			IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), r -> r.fluidIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			MOLD_CODECS.fieldOf("mold"), r -> r.mold,
			DualCodecs.INT.fieldOf("energy"), FoundryRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), FoundryRecipe::getTotalProcessTime,
			FoundryRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, FoundryRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.FOUNDRY.iconStack();
	}
}
