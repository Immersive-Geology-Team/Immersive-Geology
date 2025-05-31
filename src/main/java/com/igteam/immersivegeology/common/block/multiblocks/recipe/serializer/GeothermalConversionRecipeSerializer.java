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
		ResourceLocation startingBlockName = new ResourceLocation(json.get("startingBlock").getAsString());
		ResourceLocation transitionBlockName = new ResourceLocation(json.get("transitionBlock").getAsString());
		ResourceLocation finalBlockName = new ResourceLocation(json.get("finalBlock").getAsString());
		Block startingBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(startingBlockName));
		Block transitionBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(transitionBlockName));
		Block finalBlock = (Block)Preconditions.checkNotNull((Block)ForgeRegistries.BLOCKS.getValue(finalBlockName));
		int startingBlockIndex = json.get("startingBlockIndex").getAsInt();
		int transitionBlockIndex = json.get("transitionBlockIndex").getAsInt();
		int finalBlockIndex = json.get("finalBlockIndex").getAsInt();

		return new GeothermalConversionRecipe(resourceLocation, () -> startingBlock, () -> transitionBlock, ()-> finalBlock, startingBlockIndex, transitionBlockIndex, finalBlockIndex);
	}

	@Override
	public @Nullable GeothermalConversionRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer)
	{
		List<Block> blocks = PacketUtils.readList(buffer, (buf) -> {
			return (Block)buf.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
		});
		int startingBlockIndex = buffer.readInt();
		int transitionBlockIndex = buffer.readInt();
		int finalBlockIndex = buffer.readInt();
		return new GeothermalConversionRecipe(resourceLocation, () -> blocks.get(0), () -> blocks.get(1), ()-> blocks.get(2), startingBlockIndex, transitionBlockIndex, finalBlockIndex);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GeothermalConversionRecipe recipe)
	{
		PacketUtils.writeList(buffer, recipe.getMatchingBlocks(), (b, buf) -> {
			buf.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, b);
		});
		buffer.writeInt(recipe.blockIndexS);
		buffer.writeInt(recipe.blockIndexT);
		buffer.writeInt(recipe.blockIndexF);
	}
}
