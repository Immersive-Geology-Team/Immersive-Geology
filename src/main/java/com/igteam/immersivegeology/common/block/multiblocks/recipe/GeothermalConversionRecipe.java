/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IJEIRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class GeothermalConversionRecipe extends IESerializableRecipe implements IJEIRecipe
{
	public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<GeothermalConversionRecipe>> SERIALIZER;
	public static final CachedRecipeList<GeothermalConversionRecipe> RECIPES = new CachedRecipeList<>(IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION);

	private static HashSet<Block> usedBlocks = new HashSet<>();

	public final Supplier<Block> transitionBlock;
	public final int blockHeat;

	@Nullable
	public final Block upperTransition;
	@Nullable
	public Integer upperHeat;

	@Nullable
	public Block lowerTransition;
	@Nullable
	public Integer lowerHeat;

	public <T extends Recipe<?>> GeothermalConversionRecipe(Supplier<Block> transitionaryBlock, int blockHeat, @Nullable Pair<Block, Integer> upperBound, @Nullable Pair<Block, Integer> lowerBound)
	{
		super(TagOutput.EMPTY, IGRecipeTypes.GEOTHERMAL_EXCHANGER_CONVERTION);
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
		for(RecipeHolder<GeothermalConversionRecipe> recipe : RECIPES.getRecipes(level))
			if(recipe.value().transitionBlock.get().equals(block))
			{
				FluidState fluidState = block.defaultBlockState().getFluidState();
				if(!fluidState.isEmpty())
				{
					if(fluidState.isSource())
					{
						return recipe.value();
					}
					return null;
				}
				return recipe.value();
			}
		return null;
	}

	public static GeothermalConversionRecipe findRecipe(Level level, int blockIndex)
	{
		if(blockIndex == -1) return null;
		int i = 0;
		for(RecipeHolder<GeothermalConversionRecipe> recipe : RECIPES.getRecipes(level))
		{
			if(i == blockIndex) return recipe.value();
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
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registryAccess)
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
