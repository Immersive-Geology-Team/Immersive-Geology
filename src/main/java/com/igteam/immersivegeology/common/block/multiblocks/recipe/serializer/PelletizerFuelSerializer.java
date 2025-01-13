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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerFuel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;

public class PelletizerFuelSerializer extends IERecipeSerializer<PelletizerFuel>
{
	public PelletizerFuelSerializer() {
	}

	public ItemStack getIcon() {
		return new ItemStack(Items.CLAY_BALL);
	}

	public PelletizerFuel readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
		Ingredient input = Ingredient.fromJson(json.getAsJsonObject("input"));
		int time = GsonHelper.getAsInt(json, "time", 1200);
		return new PelletizerFuel(recipeId, input, time);
	}

	@Nullable
	public PelletizerFuel fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		Ingredient input = Ingredient.fromNetwork(buffer);
		int time = buffer.readInt();
		return new PelletizerFuel(recipeId, input, time);
	}

	public void toNetwork(FriendlyByteBuf buffer, PelletizerFuel recipe) {
		recipe.input.toNetwork(buffer);
		buffer.writeInt(recipe.burnTime);
	}
}
