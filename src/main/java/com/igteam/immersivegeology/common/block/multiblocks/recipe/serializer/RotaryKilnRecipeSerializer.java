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
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class RotaryKilnRecipeSerializer extends IERecipeSerializer<RotaryKilnRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.ROTARYKILN.iconStack();
	}

	@Override
	public RotaryKilnRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{
		Lazy<ItemStack> output = readOutput(json.get("result"));
		IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		return new RotaryKilnRecipe(resourceLocation, input, output, energy, time);
	}

	@Override
	public @Nullable RotaryKilnRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> output = readLazyStack(buffer);
		IngredientWithSize input = IngredientWithSize.read(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		return new RotaryKilnRecipe(resourceLocation, input, output, energy, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RotaryKilnRecipe recipe)
	{
		writeLazyStack(buffer, recipe.itemOutput);
		recipe.itemIn.write(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
}
