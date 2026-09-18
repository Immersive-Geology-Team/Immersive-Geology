/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import javax.annotation.Nullable;

public class IGMoldShapingSerializer implements RecipeSerializer<IGMoldShapingRecipe>
{
	private static final ShapelessRecipe.Serializer SHAPELESS = new ShapelessRecipe.Serializer();

	@Override
	public IGMoldShapingRecipe fromJson(ResourceLocation id, JsonObject json)
	{
		return wrap(SHAPELESS.fromJson(id, json));
	}

	@Nullable
	@Override
	public IGMoldShapingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
	{
		ShapelessRecipe base = SHAPELESS.fromNetwork(id, buffer);
		return base==null?null: wrap(base);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, IGMoldShapingRecipe recipe)
	{
		SHAPELESS.toNetwork(buffer, recipe);
	}

	private static IGMoldShapingRecipe wrap(ShapelessRecipe base)
	{
		return new IGMoldShapingRecipe(
				base.getId(), base.getGroup(), base.category(),
				base.getResultItem(RegistryAccess.EMPTY), base.getIngredients()
		);
	}
}
