/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonArray;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalBiomeRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collection;

public class GeothermalBiomeRecipeBuilder extends IEFinishedRecipe<GeothermalBiomeRecipeBuilder>
{
	private GeothermalBiomeRecipeBuilder() {
		super(GeothermalBiomeRecipe.SERIALIZER.get());
	}

	public GeothermalBiomeRecipeBuilder setHeat(int min_heat, int max_heat) {
		this.addWriter(json -> json.addProperty("min_heat", min_heat));
		this.addWriter(json -> json.addProperty("max_heat", max_heat));
		return this;
	}

	public static GeothermalBiomeRecipeBuilder fromBiome(ResourceKey<Biome> biome, int min_heat, int max_heat) {
		GeothermalBiomeRecipeBuilder builder = new GeothermalBiomeRecipeBuilder();
		ResourceLocation biomeId = biome.location();
		builder.addWriter(json -> json.addProperty("biome", biomeId.toString()));
		return builder.setHeat(min_heat, max_heat);
	}


	@SafeVarargs
	public static GeothermalBiomeRecipeBuilder fromTags(int min_heat, int max_heat, TagKey<Biome>... tags) {
		return fromTags(Arrays.asList(tags), min_heat, max_heat);
	}

	public static GeothermalBiomeRecipeBuilder fromTags(Collection<TagKey<Biome>> tags, int min_heat, int max_heat) {
		GeothermalBiomeRecipeBuilder builder = new GeothermalBiomeRecipeBuilder();
		JsonArray array = new JsonArray();
		for (TagKey<Biome> tag : tags) {
			array.add(tag.location().toString());
		}
		builder.addWriter(json -> json.add("biome_tags", array));
		return builder.setHeat(min_heat, max_heat);
	}
}