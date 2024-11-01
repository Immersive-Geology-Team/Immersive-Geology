/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;

public class BloomeryFuelSerializer extends IERecipeSerializer<BloomeryFuel>
{
	public BloomeryFuelSerializer() {
	}

	public ItemStack getIcon() {
		return new ItemStack(Ingredients.COAL_COKE);
	}

	public BloomeryFuel readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
		Ingredient input = Ingredient.fromJson(json.getAsJsonObject("input"));
		int time = GsonHelper.getAsInt(json, "time", 1200);
		return new BloomeryFuel(recipeId, input, time);
	}

	@Nullable
	public BloomeryFuel fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		Ingredient input = Ingredient.fromNetwork(buffer);
		int time = buffer.readInt();
		return new BloomeryFuel(recipeId, input, time);
	}

	public void toNetwork(FriendlyByteBuf buffer, BloomeryFuel recipe) {
		recipe.input.toNetwork(buffer);
		buffer.writeInt(recipe.burnTime);
	}
}
