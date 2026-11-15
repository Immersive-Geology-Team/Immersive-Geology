/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.utils.FastEither;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalBiomeRecipe;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GeothermalBiomeRecipeBuilder extends IGMultiblockRecipeBuilder<GeothermalBiomeRecipeBuilder>
{
	private final FastEither<Holder<Biome>, List<TagKey<Biome>>> biomes;
	private int min_heat;
	private int max_heat;

	private GeothermalBiomeRecipeBuilder(FastEither<Holder<Biome>, List<TagKey<Biome>>> biomes)
	{
		this.biomes = biomes;
	}

	public GeothermalBiomeRecipeBuilder setHeat(int min_heat, int max_heat)
	{
		this.min_heat = min_heat;
		this.max_heat = max_heat;
		return this;
	}

	public static GeothermalBiomeRecipeBuilder fromBiome(Holder<Biome> biome, int min_heat, int max_heat)
	{
		return new GeothermalBiomeRecipeBuilder(FastEither.left(biome)).setHeat(min_heat, max_heat);
	}

	@SafeVarargs
	public static GeothermalBiomeRecipeBuilder fromTags(int min_heat, int max_heat, TagKey<Biome>... tags)
	{
		return fromTags(Arrays.asList(tags), min_heat, max_heat);
	}

	public static GeothermalBiomeRecipeBuilder fromTags(Collection<TagKey<Biome>> tags, int min_heat, int max_heat)
	{
		return new GeothermalBiomeRecipeBuilder(FastEither.right(List.copyOf(tags))).setHeat(min_heat, max_heat);
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		GeothermalBiomeRecipe recipe = biomes.map(
				biome -> new GeothermalBiomeRecipe(biome, min_heat, max_heat),
				tags -> new GeothermalBiomeRecipe(tags, min_heat, max_heat)
		);
		out.accept(name, recipe, null, getConditions());
	}
}