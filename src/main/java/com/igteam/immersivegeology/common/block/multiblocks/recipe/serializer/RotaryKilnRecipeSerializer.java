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
import com.google.gson.JsonSyntaxException;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
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
		int time = GsonHelper.getAsInt(json, "time");
		int heat = GsonHelper.getAsInt(json, "heat");
		validate(resourceLocation, time, heat);
		return new RotaryKilnRecipe(resourceLocation, input, output, time, heat);
	}

	@Override
	public @Nullable RotaryKilnRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		Lazy<ItemStack> output = readLazyStack(buffer);
		IngredientWithSize input = IngredientWithSize.read(buffer);
		int time = buffer.readInt();
		int heat = buffer.readInt();
		return new RotaryKilnRecipe(resourceLocation, input, output, time, heat);
	}

	/**
	 * A time of zero divides by zero when Immersive Engineering works out the per-tick energy,
	 * and a heat the kiln cannot physically reach would leave it heating forever, so both are
	 * rejected at load rather than at runtime.
	 */
	private static void validate(ResourceLocation id, int time, int heat)
	{
		if(time <= 0)
			throw new JsonSyntaxException("Rotary Kiln recipe "+id+" has a time of "+time+", it must be at least 1 tick");
		if(heat < 0||heat > RotaryKilnLogic.EHV_HEAT_CAP)
			throw new JsonSyntaxException("Rotary Kiln recipe "+id+" requires a heat of "+heat+", it must be between 0 and "+RotaryKilnLogic.EHV_HEAT_CAP);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RotaryKilnRecipe recipe)
	{
		writeLazyStack(buffer, recipe.itemOutput);
		recipe.itemIn.write(buffer);
		buffer.writeInt(recipe.getTotalProcessTime());
		buffer.writeInt(recipe.getHeatRequired());
	}
}
