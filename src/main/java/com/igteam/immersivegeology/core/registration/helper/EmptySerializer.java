/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration.helper;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import org.jetbrains.annotations.Nullable;

public class EmptySerializer extends IERecipeSerializer<EmptyRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public EmptyRecipe readFromJson(ResourceLocation resourceLocation, JsonObject jsonObject, IContext iContext)
	{
		return new EmptyRecipe(resourceLocation);
	}

	@Override
	public @Nullable EmptyRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf)
	{
		return new EmptyRecipe(resourceLocation);
	}

	@Override
	public void toNetwork(FriendlyByteBuf friendlyByteBuf, EmptyRecipe emptyRecipe)
	{}
}
