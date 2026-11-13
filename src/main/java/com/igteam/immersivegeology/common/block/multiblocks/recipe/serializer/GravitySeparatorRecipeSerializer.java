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
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class GravitySeparatorRecipeSerializer extends IERecipeSerializer<GravitySeparatorRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, GravitySeparatorRecipe> CODECS = DualCompositeMapCodecs.composite(
			DualCodecs.INGREDIENT.fieldOf("input"), r -> r.itemIn,
			TagOutput.CODECS.fieldOf("result"), r -> r.itemOutput,
			TagOutput.CODECS.fieldOf("byproduct"), r -> r.itemByproduct,
			DualCodecs.FLOAT.fieldOf("byproduct_chance"), GravitySeparatorRecipe::getChance,
			DualCodecs.INT.fieldOf("water"), GravitySeparatorRecipe::getTotalProcessWater,
			DualCodecs.INT.fieldOf("time"), GravitySeparatorRecipe::getTotalProcessTime,
			GravitySeparatorRecipe::new
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, GravitySeparatorRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GRAVITY_SEPARATOR.iconStack();
	}
}
