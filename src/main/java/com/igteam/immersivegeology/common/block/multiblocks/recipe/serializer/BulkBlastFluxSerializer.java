/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BulkBlastFluxRecipe;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;

public class BulkBlastFluxSerializer extends IERecipeSerializer<BulkBlastFluxRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return MetalEnum.Calcium.getStack(ItemCategoryFlags.METAL_OXIDE);
	}

	@Override
	public BulkBlastFluxRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
	{
		Ingredient input = Ingredient.fromJson(json.get("input"));
		int fluxValue = GsonHelper.getAsInt(json, "flux_value", 1);
		return new BulkBlastFluxRecipe(recipeId, input, fluxValue);
	}

	@Nullable
	@Override
	public BulkBlastFluxRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
	{
		Ingredient input = Ingredient.fromNetwork(buffer);
		int fluxValue = buffer.readVarInt();
		return new BulkBlastFluxRecipe(recipeId, input, fluxValue);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BulkBlastFluxRecipe recipe)
	{
		recipe.input.toNetwork(buffer);
		buffer.writeVarInt(recipe.fluxValue);
	}
}
