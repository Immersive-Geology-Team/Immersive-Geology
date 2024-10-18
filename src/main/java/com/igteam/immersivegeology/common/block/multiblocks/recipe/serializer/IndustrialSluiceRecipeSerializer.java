/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
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

		NonNullList<StackWithChance> byproducts = readByproductsFromJson(json);

		int time = GsonHelper.getAsInt(json, "time");
		int water = GsonHelper.getAsInt(json, "water");
		return new IndustrialSluiceRecipe(resourceLocation, input, primary, byproducts, water, time);
	}

	private NonNullList<StackWithChance> readByproductsFromJson(JsonObject obj) {
		// Probably not the most effective way to store the information, but if it works -\('-')/- ~Muddykat
		int amount_of_byproducts = GsonHelper.getAsInt(obj, "amount_of_byproducts");
		NonNullList<StackWithChance> list = NonNullList.createWithCapacity(amount_of_byproducts);

		JsonArray jsonArray = GsonHelper.getAsJsonArray(obj, "byproducts");

		for (int index = 0; index < amount_of_byproducts; index++) {
			JsonObject byproductObject = jsonArray.get(index).getAsJsonObject();
			list.add(readConditionalStackWithChance(byproductObject, IContext.EMPTY));
		}

		return list;
	}

	@Override
	public @Nullable IndustrialSluiceRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> primary = readLazyStack(buffer);

		NonNullList<StackWithChance> byproducts = readByproducts(buffer);

		Ingredient input = Ingredient.fromNetwork(buffer);
		int time = buffer.readInt();
		int water = buffer.readInt();
		return new IndustrialSluiceRecipe(resourceLocation, input, primary, byproducts, water, time);
	}

	private NonNullList<StackWithChance> readByproducts(FriendlyByteBuf buffer)
	{
		int size = buffer.readInt();
		NonNullList<StackWithChance> item_list = NonNullList.createWithCapacity(size);
		for(int index = 0; index < size; index++)
		{
			item_list.add(StackWithChance.read(buffer));
		}

		return item_list;
	}

	private void writeByproducts(FriendlyByteBuf buffer, IndustrialSluiceRecipe recipe)
	{
		NonNullList<StackWithChance> byproducts = recipe.getByproducts();

		int size = byproducts.size();

		buffer.writeInt(size);
		for(int index = 0; index < size; index++)
		{
			byproducts.get(index).write(buffer);
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
