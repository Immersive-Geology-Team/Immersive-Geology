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
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class BloomeryRecipeSerializer extends IERecipeSerializer<BloomeryRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.BLOOMERY.iconStack();
	}

	@Override
	public BloomeryRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(json.get("result"));
		IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		int time = GsonHelper.getAsInt(json, "time");
		return new BloomeryRecipe(resourceLocation, input, output, time);
	}

	@Override
	public @Nullable BloomeryRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		
		Lazy<ItemStack> output = readLazyStack(buffer);
		IngredientWithSize input = IngredientWithSize.read(buffer);
		int time = buffer.readInt();
		return new BloomeryRecipe(resourceLocation, input, output, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
	{
		
		writeLazyStack(buffer, recipe.result);
		recipe.input.write(buffer);
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
