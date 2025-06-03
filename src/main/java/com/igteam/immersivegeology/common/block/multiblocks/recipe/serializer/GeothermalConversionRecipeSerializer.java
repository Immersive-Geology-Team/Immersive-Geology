/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.network.PacketUtils;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeothermalConversionRecipeSerializer extends IERecipeSerializer<GeothermalConversionRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}

	@Override
	public GeothermalConversionRecipe readFromJson(ResourceLocation resourceLocation, JsonObject json, IContext iContext)
	{

		ResourceLocation transitionBlockName = new ResourceLocation(json.get("transitionBlock").getAsString());
		Block transitionBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(transitionBlockName));
		int transitionBlockHeat = json.get("blockHeat").getAsInt();

		boolean hasUpper = json.has("upperBoundBlock");
		boolean hasLower = json.has("lowerBoundBlock");
		Pair<Block, Integer> upperBound = null;
		Pair<Block, Integer> lowerBound = null;

		if(hasUpper)
		{
			ResourceLocation upperBoundBlockName = new ResourceLocation(json.get("upperBoundBlock").getAsString());
			Block upperBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(upperBoundBlockName));
			int upperHeat = json.get("upperHeat").getAsInt();
			upperBound = Pair.of(upperBlock, upperHeat);
		}

		if(hasLower)
		{
			ResourceLocation lowerBoundBlockName = new ResourceLocation(json.get("lowerBoundBlock").getAsString());
			Block lowerBoundBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(lowerBoundBlockName));
			int upperHeat = json.get("lowerHeat").getAsInt();
			lowerBound = Pair.of(lowerBoundBlock, upperHeat);
		}

		return new GeothermalConversionRecipe(resourceLocation, () -> transitionBlock, transitionBlockHeat, upperBound, lowerBound);
	}

	@Override
	public @Nullable GeothermalConversionRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		List<Block> blocks = PacketUtils.readList(buffer, (buf) -> {
			return (Block)buf.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
		});

		Block baseBlock = blocks.get(0);
		Block upperBlock = blocks.get(1);
		Block lowerBlock = blocks.get(2);

		int transitionBlockHeat = buffer.readInt();
		boolean hasUpper = upperBlock != Blocks.BARRIER;
		boolean hasLower = lowerBlock != Blocks.BARRIER;

		Pair<Block, Integer> upperBound = null;
		Pair<Block, Integer> lowerBound = null;

		if(hasUpper)
		{
			upperBound = Pair.of(upperBlock, buffer.readInt());
		}

		if(hasLower)
		{
			lowerBound = Pair.of(lowerBlock, buffer.readInt());
		}

		return new GeothermalConversionRecipe(resourceLocation, () -> baseBlock, transitionBlockHeat, upperBound, lowerBound);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GeothermalConversionRecipe recipe)
	{
		PacketUtils.writeList(buffer, recipe.getMatchingBlocks(), (b, buf) -> {
			buf.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, b);
		});
		buffer.writeInt(recipe.blockHeat);
		if(recipe.upperHeat != null) buffer.writeInt(recipe.upperHeat);
		if(recipe.lowerHeat != null) buffer.writeInt(recipe.lowerHeat);
	}
}
