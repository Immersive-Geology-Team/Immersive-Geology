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
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class PelletizerRecipeSerializer extends IERecipeSerializer<PelletizerRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, PelletizerRecipe> CODECS = DualCompositeMapCodecs.composite(
			IngredientWithSize.CODECS.fieldOf("input"), r -> r.itemIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			DualCodecs.INT.fieldOf("energy"), PelletizerRecipe::getTotalProcessEnergy,
			DualCodecs.INT.fieldOf("time"), PelletizerRecipe::getTotalProcessTime,
			PelletizerRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, PelletizerRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.PELLETIZER.iconStack();
	}
}
