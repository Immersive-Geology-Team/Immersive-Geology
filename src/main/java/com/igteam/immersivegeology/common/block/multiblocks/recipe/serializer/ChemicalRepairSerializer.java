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
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRepairRecipe;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.Nullable;

public class ChemicalRepairSerializer extends IERecipeSerializer<ChemicalRepairRecipe>
{
	public static final DualMapCodec<RegistryFriendlyByteBuf, ChemicalRepairRecipe> CODECS = DualCompositeMapCodecs.composite(
			DualCodecs.INGREDIENT.fieldOf("input"), r -> r.input,
			DualCodecs.INT.optionalFieldOf("time", 10), r -> r.burnTime,
			ChemicalRepairRecipe::new
	);

	public ChemicalRepairSerializer() {
	}

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, ChemicalRepairRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Ingredients.COAL_COKE);
	}
}
