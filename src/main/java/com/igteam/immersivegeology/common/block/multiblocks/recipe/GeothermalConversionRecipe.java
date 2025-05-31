/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

public class GeothermalConversionRecipe extends IESerializableRecipe implements IJEIRecipe
{
	public static RegistryObject<IERecipeSerializer<GeothermalConversionRecipe>> SERIALIZER;
	public static final CachedRecipeList<GeothermalConversionRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION);

	private static HashSet<Block> usedBlocks = new HashSet<>();

	public final Lazy<Block> startingBlock, transitionBlock, finalBlock;
	public final Integer blockIndexS, blockIndexT, blockIndexF;

	public <T extends Recipe<?>> GeothermalConversionRecipe(ResourceLocation id, Lazy<Block> startingBlock, Lazy<Block> transitionBlock, Lazy<Block> finalBlock, int blockIndexS, int blockIndexT, int blockIndexF)
	{
		super(LAZY_EMPTY, IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION, id);
		this.startingBlock = startingBlock;
		this.transitionBlock = transitionBlock;
		this.finalBlock = finalBlock;

		this.blockIndexS = blockIndexS;
		this.blockIndexT = blockIndexT;
		this.blockIndexF = blockIndexF;

		if(usedBlocks.contains(startingBlock.get())) IGLib.IG_LOGGER.error("INVALID Geothermal Conversion Recipe (No recipe can share the same blocks)");
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	public static GeothermalConversionRecipe findRecipe(Level level, Block block)
	{
		for(GeothermalConversionRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.startingBlock.get().equals(block) || recipe.transitionBlock.get().equals(block) || recipe.finalBlock.get().equals(block))
				return recipe;
		return null;
	}

	public static GeothermalConversionRecipe findRecipe(Level level, int blockIndex)
	{
		for(GeothermalConversionRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.blockIndexS == blockIndex || recipe.blockIndexT == blockIndex || recipe.blockIndexF == blockIndex)
				return recipe;
		return null;
	}

	public static @NotNull Block getBlockFromIndex(Level level, int blockIndex)
	{
		GeothermalConversionRecipe recipe = findRecipe(level, blockIndex);
		if(recipe == null) return Blocks.AIR;
		return blockIndex == recipe.blockIndexS ? recipe.startingBlock.get() : (blockIndex == recipe.blockIndexT ? recipe.transitionBlock.get() : recipe.finalBlock.get());
	}

	@Override
	protected IERecipeSerializer<?> getIESerializer()
	{
		return SERIALIZER.get();
	}


	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess)
	{
		return ItemStack.EMPTY;
	}

	public List<Block> getMatchingBlocks()
	{
		return List.of(startingBlock.get(), transitionBlock.get(), finalBlock.get());
	}

	public boolean hasBlock(Block b)
	{
		return startingBlock.get().equals(b) || transitionBlock.get().equals(b) || finalBlock.get().equals(b);
	}
}
