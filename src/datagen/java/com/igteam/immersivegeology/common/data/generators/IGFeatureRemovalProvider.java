/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.common.world.IEOreFeature;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class IGFeatureRemovalProvider implements DataProvider {
	private final PackOutput output;
	private final Map<ResourceLocation, Supplier<JsonElement>> elements = new HashMap<>();

	// Constructor for setting up the PackOutput
	public IGFeatureRemovalProvider(PackOutput output) {
		this.output = output;
		init();
	}

	private void init()
	{
		List<ResourceKey<ConfiguredFeature<?,?>>> features = List.of(OreFeatures.ORE_COPPPER_SMALL, OreFeatures.ORE_GOLD, OreFeatures.ORE_GOLD_BURIED,
				OreFeatures.ORE_IRON, OreFeatures.ORE_IRON_SMALL, OreFeatures.ORE_NETHER_GOLD);

		for(ResourceKey<ConfiguredFeature<?,?>> feature : features)
		{
			removeFeature(feature.location());
		}
		removeFeature(new ResourceLocation("immersiveengineering", "bauxite"));
		removeFeature(new ResourceLocation("immersiveengineering", "deep_nickel"));
		removeFeature(new ResourceLocation("immersiveengineering", "lead"));
		removeFeature(new ResourceLocation("immersiveengineering", "nickel"));
		removeFeature(new ResourceLocation("immersiveengineering", "silver"));
		removeFeature(new ResourceLocation("immersiveengineering", "uranium"));

	}

	// Method to create and add a feature removal entry
	public void removeFeature(ResourceLocation location) {
		JsonObject featureRemovalJson = new JsonObject();
		featureRemovalJson.addProperty("type", "immersivegeology:ore_removal");

		// Handling single biome or tag
		featureRemovalJson.add("config", new JsonObject());

		// Save the generated JSON under the specified name
		elements.put(location, () -> featureRemovalJson);
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		// We will return a CompletableFuture that generates multiple JSON files
		List<CompletableFuture<?>> futures = new ArrayList<>();

		elements.forEach((jsonName, supplier) -> {
			JsonElement jsonElement = supplier.get();

			// Determine the path for each JSON file
			Path path = output.getOutputFolder().resolve("data/"+jsonName.getNamespace()+"/worldgen/configured_feature/" + jsonName.getPath() + ".json");

			// Add each individual save task to the futures list
			CompletableFuture<?> future = DataProvider.saveStable(cache, jsonElement, path);
			futures.add(future);
		});

		// Return a CompletableFuture that completes when all the individual save tasks are done
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}

	@Override
	public String getName() {
		return "Feature Removal Data Generator";
	}
}