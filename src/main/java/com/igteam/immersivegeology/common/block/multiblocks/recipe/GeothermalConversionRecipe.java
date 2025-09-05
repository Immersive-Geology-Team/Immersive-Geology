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
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class GeothermalConversionRecipe extends IESerializableRecipe implements IJEIRecipe
{
	public static RegistryObject<IERecipeSerializer<GeothermalConversionRecipe>> SERIALIZER;
	public static final CachedRecipeList<GeothermalConversionRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION);

	private static HashSet<Block> usedBlocks = new HashSet<>();

	public final Lazy<Block> transitionBlock;
	public final int blockHeat;

	@Nullable
	public final Block upperTransition;
	@Nullable
	public Integer upperHeat;

	@Nullable
	public Block lowerTransition;
	@Nullable
	public Integer lowerHeat;

	public <T extends Recipe<?>> GeothermalConversionRecipe(ResourceLocation id, Lazy<Block> transitionaryBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound)
	{
		super(LAZY_EMPTY, IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION, id);
		this.transitionBlock = transitionaryBlock;
		this.blockHeat = blockHeat;

		this.upperTransition = upperBound == null ? null : upperBound.getFirst();
		this.upperHeat = upperBound == null ? null :  upperBound.getSecond();

		this.lowerTransition = lowerBound == null ? null : lowerBound.getFirst();
		this.lowerHeat = lowerBound == null ? null : lowerBound.getSecond();
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SERIALIZER.get();
	}

	public static GeothermalConversionRecipe findRecipe(Level level, Block block)
	{
		for(GeothermalConversionRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.transitionBlock.get().equals(block))
			{
				FluidState fluidState = block.defaultBlockState().getFluidState();
				if(!fluidState.isEmpty())
				{
					if(fluidState.isSource())
					{
						return recipe;
					}
					return null;
				}
				return recipe;
			}
		return null;
	}

	public static GeothermalConversionRecipe findRecipe(Level level, int blockIndex)
	{
		if(blockIndex == -1) return null;
		int i = 0;
		for(GeothermalConversionRecipe recipe : RECIPES.getRecipes(level))
		{
			if(i == blockIndex) return recipe;
			i++;
		}
		return null;
	}

	public static @NotNull Block getBlockFromIndex(Level level, int blockIndex)
	{
		GeothermalConversionRecipe recipe = findRecipe(level, blockIndex);
		if(recipe == null) return Blocks.AIR;
		return recipe.transitionBlock.get();
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
		if(upperTransition != null && lowerTransition != null) return List.of(transitionBlock.get(), upperTransition, lowerTransition);
		if(upperTransition != null) return List.of(transitionBlock.get(), upperTransition, Blocks.BARRIER);
		if(lowerTransition != null) return List.of(transitionBlock.get(), Blocks.BARRIER, lowerTransition);
		return List.of(transitionBlock.get(), Blocks.BARRIER, Blocks.BARRIER);
	}
}
