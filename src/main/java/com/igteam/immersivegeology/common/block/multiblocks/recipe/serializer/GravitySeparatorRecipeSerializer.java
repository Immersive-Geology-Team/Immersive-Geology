/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class GravitySeparatorRecipeSerializer extends IERecipeSerializer<GravitySeparatorRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GRAVITY_SEPARATOR.iconStack();
	}

	@Override
	public GravitySeparatorRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(json.get("result"));
		Lazy<ItemStack> byproduct = readOutput(json.get("byproduct"));
		float chance = GsonHelper.getAsFloat(json, "byproduct_chance");
		Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
		int time = GsonHelper.getAsInt(json, "time");
		int water = GsonHelper.getAsInt(json, "water");
		return new GravitySeparatorRecipe(resourceLocation, input, output, byproduct, chance, water, time);
	}

	@Override
	public @Nullable GravitySeparatorRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> output = readLazyStack(buffer);
		Lazy<ItemStack> byproduct = readLazyStack(buffer);
		float chance = buffer.readFloat();
		Ingredient input = Ingredient.fromNetwork(buffer);
		int time = buffer.readInt();
		int water = buffer.readInt();
		return new GravitySeparatorRecipe(resourceLocation, input, output, byproduct, chance, water, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GravitySeparatorRecipe recipe)
	{
		writeLazyStack(buffer, recipe.itemOutput);
		writeLazyStack(buffer, recipe.itemByproduct);
		buffer.writeFloat(recipe.getChance());
		recipe.itemIn.toNetwork(buffer);
		buffer.writeInt(recipe.getTotalProcessTime());
		buffer.writeInt(recipe.getTotalProcessWater());
	}
}
