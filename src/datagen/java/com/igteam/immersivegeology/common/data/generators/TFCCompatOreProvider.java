/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TFCCompatOreProvider implements DataProvider {
	private final PackOutput output;
	private final Map<ResourceLocation, Supplier<JsonElement>> elements = new HashMap<>();

	// Constructor for setting up the PackOutput
	public TFCCompatOreProvider(PackOutput output) {
		this.output = output;
		init();
	}

	private void init() {
		// --- New soil disc generation ---
		// Define the list of state mappings for soil disc changes.
		// Define parameters for the soil disc feature.
		int minRadius = 3;
		int maxRadius = 5;
		int height = 3;
		// You can change "soil_disc_example" to any identifier you wish.
		generateSoilDisc(minRadius, maxRadius, height, MineralEnum.Carnallite);
		generateSoilDisc(minRadius, maxRadius, height, MineralEnum.Rocksalt);
		generateSoilDisc(minRadius, maxRadius, height, MineralEnum.Saltpeter);
	}

	// --- New soil disc generation method ---
	/**
	 * Generates a JSON file for a soil disc feature.
	 *
	 * @param minRadius    Minimum radius for the disc.
	 * @param maxRadius    Maximum radius for the disc.
	 * @param height       Height of the disc.
	 * @param mineral	   Mineral for the evaporate to use for the spawning
	 */
	public void generateSoilDisc(int minRadius, int maxRadius, int height, MaterialInterface<?> mineral) {
		JsonObject root = new JsonObject();
		List<StateMapping> stateMapping = new ArrayList<>();
		String name = mineral.getName().toLowerCase() + "_tfc";
		stateMapping.add(new StateMapping("tfc:dirt/sandy_loam", new ResourceLocation(IGLib.MODID, BlockCategoryFlags.EVAPORATE.getRegistryKey(mineral)).toString()));
		stateMapping.add(new StateMapping("tfc:dirt/silty_loam", new ResourceLocation(IGLib.MODID, BlockCategoryFlags.EVAPORATE.getRegistryKey(mineral)).toString()));
		stateMapping.add(new StateMapping("tfc:grass/sandy_loam", new ResourceLocation(IGLib.MODID, BlockCategoryFlags.EVAPORATE.getRegistryKey(mineral)).toString()));
		// Optional comment (note that comments in JSON are not officially supported
		// but some parsers ignore them)
		root.addProperty("type", "tfc:soil_disc");

		JsonObject config = new JsonObject();
		config.addProperty("min_radius", minRadius);
		config.addProperty("max_radius", maxRadius);
		config.addProperty("height", height);

		JsonArray states = new JsonArray();
		for (StateMapping mapping : stateMapping) {
			JsonObject stateObj = new JsonObject();
			stateObj.addProperty("replace", mapping.replace);
			stateObj.addProperty("with", mapping.with);
			states.add(stateObj);
		}
		config.add("states", states);
		root.add("config", config);

		// Save the generated JSON file under an appropriate path.
		JsonObject placedConfigJson = new JsonObject();
		placedConfigJson.addProperty("feature", "immersivegeology:" + name);
		placedConfigJson.add("placement", new JsonArray());

		elements.put(new ResourceLocation("immersivegeology", "worldgen/placed_feature/" + name), () -> placedConfigJson);
		elements.put(new ResourceLocation("immersivegeology", "worldgen/configured_feature/" + name), () -> root);
	}

	// --- Helper class for state mapping ---
	private static class StateMapping {
		public final String replace;
		public final String with;

		public StateMapping(String replace, String with) {
			this.replace = replace;
			this.with = with;
		}
	}

	// Updated generateVein method using MineralEnum
	public void generateVein(String name, MaterialInterface<?> mineral, long random_name) {
		JsonObject veinConfigJson = new JsonObject();
		veinConfigJson.addProperty("type", "immersivegeology:ig_ore");

		JsonObject config = new JsonObject();
		config.addProperty("entry", mineral.getName());
		config.addProperty("temp_range_min", mineral.getName());
		config.addProperty("temp_range_max", mineral.getName());
		config.addProperty("downfall_min", mineral.getName());
		config.addProperty("downfall_max", mineral.getName());
		config.addProperty("random_name", random_name);

		//JsonArray blocksArray = new JsonArray();

		// Loop over all StoneEnum values to determine block replacements
//		for (StoneEnum stone : StoneEnum.values()) {
//			if(!stone.hasFlag(ModFlags.TFC)) continue;
//			if (!mineral.instance().acceptableStoneType(stone.instance()) || mineral.instance().checkExistingImplementation(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK)) {
//				continue; // Skip if not acceptable
//			}
//
//			// Get the blocks for the different ore richness types
//			List<Block> blocks = List.of(
//					mineral.getOreBlock(stone, OreRichness.POOR).asBlock(),
//					mineral.getOreBlock(stone, OreRichness.NORMAL).asBlock(),
//					mineral.getOreBlock(stone, OreRichness.RICH).asBlock()
//			);
//
//			// Create a block replacement entry for this stone type
//			JsonObject blockObj = new JsonObject();
//			JsonArray replaceArray = new JsonArray();
//			replaceArray.add(stone.getTFCStoneLoc());
//			blockObj.add("replace", replaceArray);
//
//			JsonArray withArray = new JsonArray();
//			for (Block block : blocks) {
//				JsonObject weightObj = new JsonObject();
//				weightObj.addProperty("block", block.getDescriptionId().replaceFirst("block\\.", "").replaceFirst("\\.", ":")); // Get the block's registry name
//				weightObj.addProperty("weight", getWeightForOreRichness(block)); // Use a custom method to calculate weight
//				withArray.add(weightObj);
//			}
//			blockObj.add("with", withArray);
//			blocksArray.add(blockObj);
//		}

		//config.add("blocks", blocksArray);
		veinConfigJson.add("config", config);

		JsonObject placedConfigJson = new JsonObject();
		placedConfigJson.addProperty("feature", "immersivegeology:" + name);
		placedConfigJson.add("placement", new JsonArray());

		// Save the generated JSON under the correct path
		elements.put(new ResourceLocation("immersivegeology", "worldgen/configured_feature/" + name), () -> veinConfigJson);
		elements.put(new ResourceLocation("immersivegeology", "worldgen/placed_feature/" + name), () -> placedConfigJson);
	}

	// Method to calculate weight based on OreRichness
	private int getWeightForOreRichness(Block block) {
		if(block instanceof IOreBlock)
		{
			if(((IOreBlock)block).getOreRichness().equals(OreRichness.POOR))
			{
				return 35;
			}
			else if(((IOreBlock)block).getOreRichness().equals(OreRichness.NORMAL))
			{
				return 40;
			}
			else
			{
				return 25; // For RICH
			}
		} else {
			return 20;
		}
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		// Return a CompletableFuture that generates multiple JSON files
		List<CompletableFuture<?>> futures = new ArrayList<>();

		elements.forEach((jsonName, supplier) -> {
			JsonElement jsonElement = supplier.get();

			// Path for the generated JSON files
			Path path = output.getOutputFolder().resolve("data/" + jsonName.getNamespace() + "/" + jsonName.getPath() + ".json");

			// Add the save task to the futures list
			CompletableFuture<?> future = DataProvider.saveStable(cache, jsonElement, path);
			futures.add(future);
		});

		// Return a CompletableFuture that completes when all save tasks are done
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}

	@Override
	public String getName() {
		return "Feature Data Generator";
	}
}