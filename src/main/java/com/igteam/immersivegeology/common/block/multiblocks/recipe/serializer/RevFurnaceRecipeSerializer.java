/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class RevFurnaceRecipeSerializer extends IERecipeSerializer<RevFurnaceRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.REVERBERATION_FURNACE.iconStack();
	}

	@Override
	public RevFurnaceRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(json.get("result"));
		Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
		int waste_amount = GsonHelper.getAsInt(json, "waste");
		int time = GsonHelper.getAsInt(json, "time");
		return new RevFurnaceRecipe(resourceLocation, input, output, waste_amount, time);
	}

	@Override
	public @Nullable RevFurnaceRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> output = readLazyStack(buffer);
		Ingredient input = Ingredient.fromNetwork(buffer);
		int waste = buffer.readInt();
		int time = buffer.readInt();
		return new RevFurnaceRecipe(resourceLocation, input, output, waste, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RevFurnaceRecipe recipe)
	{
		writeLazyStack(buffer, recipe.result);
		recipe.input.write(buffer);
		buffer.writeInt(recipe.getWasteAmount());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
