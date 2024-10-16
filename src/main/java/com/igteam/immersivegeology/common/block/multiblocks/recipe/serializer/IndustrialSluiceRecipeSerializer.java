/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.IndustrialSluiceRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import mezz.jei.core.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class IndustrialSluiceRecipeSerializer extends IERecipeSerializer<IndustrialSluiceRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.INDUSTRIAL_SLUICE.iconStack();
	}

	@Override
	public IndustrialSluiceRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{

		Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));

		Lazy<ItemStack> primary = readOutput(json.get("result"));

		NonNullList<ItemStack> byproducts = readByproductsFromJson(json);
		NonNullList<Float> chances = null;

		int time = GsonHelper.getAsInt(json, "time");
		int water = GsonHelper.getAsInt(json, "water");
		return new IndustrialSluiceRecipe(resourceLocation, input, primary, byproducts, chances, water, time);
	}

	private NonNullList<ItemStack> readByproductsFromJson(JsonObject obj)
	{
		// Probably not the most effective way to store the information, but if it works -\('-')/- ~Muddykat
		int amount_of_byproducts = GsonHelper.getAsInt(obj, "amount_of_byproducts");
		NonNullList<ItemStack> list = NonNullList.createWithCapacity(amount_of_byproducts);

		JsonArray jsonArray = GsonHelper.getAsJsonArray(obj, "byproducts");
		JsonObject itemObjectArray = jsonArray.getAsJsonObject();

		for(int index = 0; index < amount_of_byproducts; index++)
		{
			list.add(ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(itemObjectArray, "byproduct_" + index).getAsJsonObject()));
		}

		return list;
	}

	@Override
	public @Nullable IndustrialSluiceRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> primary = readLazyStack(buffer);

		Pair<NonNullList<Float>, NonNullList<ItemStack>> pair = readByproducts(buffer);
		NonNullList<Float> chances = pair.first();
		NonNullList<ItemStack> byproducts = pair.second();

		Ingredient input = Ingredient.fromNetwork(buffer);
		int time = buffer.readInt();
		int water = buffer.readInt();
		return new IndustrialSluiceRecipe(resourceLocation, input, primary, byproducts, chances, water, time);
	}

	private Pair<NonNullList<Float>, NonNullList<ItemStack>> readByproducts(FriendlyByteBuf buffer)
	{
		int size = buffer.readInt();
		NonNullList<ItemStack> item_list = NonNullList.createWithCapacity(size);
		NonNullList<Float> chance_list = NonNullList.createWithCapacity(size);
		for(int index = 0; index < size; index++)
		{
			item_list.add(buffer.readItem());
			chance_list.add(buffer.readFloat());
		}

		return new Pair<>(chance_list, item_list);
	}

	private void writeByproducts(FriendlyByteBuf buffer, IndustrialSluiceRecipe recipe)
	{
		NonNullList<ItemStack> byproducts = recipe.getByproducts();
		NonNullList<Float> chances = recipe.getByproductChance();

		int chance_amount = chances.size();
		int size = byproducts.size();

		buffer.writeInt(size);
		for(int index = 0; index < size; index++)
		{
			buffer.writeItem(byproducts.get(index));
			buffer.writeFloat(chances.get(index % chance_amount));
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, IndustrialSluiceRecipe recipe)
	{
		writeLazyStack(buffer, recipe.itemOutput);
		writeByproducts(buffer, recipe);
		recipe.itemIn.toNetwork(buffer);
		buffer.writeInt(recipe.getTotalProcessTime());
		buffer.writeInt(recipe.getTotalProcessWater());
	}
}
