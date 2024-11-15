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
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.OrePattern;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class IGAtlasProvider implements DataProvider {
	private final PackOutput output;
	private final Map<String, Supplier<JsonElement>> elements = new HashMap<>();

	public IGAtlasProvider(PackOutput output) {
		this.output = output;
		setupAtlasJson();
	}

	private void setupAtlasJson() {
		JsonObject atlasJson = new JsonObject();
		JsonArray sourcesArray = new JsonArray();

		List<String> textures = new ArrayList<>();
		List<String> vein_types = Arrays.stream(OrePattern.values()).map(OrePattern::getName).toList();
		for(String  vein_type : vein_types)
		{
			for(OreRichness richness : OreRichness.values())
			{
				textures.add("block/greyscale/rock/vein/" + vein_type + "/" + richness.name().toLowerCase() + "_1");
				textures.add("block/greyscale/rock/vein/" + vein_type + "/" + richness.name().toLowerCase() + "_2");
			}
		}
		// Add paletted permutations for metals and minerals
		addPalettedPermutations(sourcesArray, "metals", MetalEnum.getAtlasPermutations(), textures);
		addPalettedPermutations(sourcesArray, "minerals", MineralEnum.getAtlasPermutations(), textures);

		atlasJson.add("sources", sourcesArray);
		elements.put("atlas", () -> atlasJson);
	}

	private void addDirectorySource(JsonArray sourcesArray, String source, String prefix) {
		JsonObject sourceObj = new JsonObject();
		sourceObj.addProperty("type", "directory");
		sourceObj.addProperty("source", source);
		sourceObj.addProperty("prefix", prefix);
		sourcesArray.add(sourceObj);
	}

	private void addPalettedPermutations(JsonArray sourcesArray, String paletteKey, List<String> paletteItems, List<String> textureLocations) {
		JsonObject paletteObj = new JsonObject();
		paletteObj.addProperty("type", "paletted_permutations");

		JsonArray texturesArray = new JsonArray();
		textureLocations.forEach(t -> texturesArray.add(IGLib.MODID + ":" + t));

		paletteObj.add("textures", texturesArray);

		paletteObj.addProperty("palette_key", "immersivegeology:color_palettes/" + paletteKey);

		JsonObject permutationsObj = new JsonObject();
		for (String item : paletteItems) {
			if(item.equalsIgnoreCase("kaolinite")) continue;
			permutationsObj.addProperty(item.toLowerCase().substring(item.lastIndexOf('/')+1) + "_" + item.substring(0,item.lastIndexOf('/')), "immersivegeology:color_palettes/" + paletteKey + "/" + item.toLowerCase());
		}
		paletteObj.add("permutations", permutationsObj);

		sourcesArray.add(paletteObj);
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		JsonObject json = new JsonObject();
		elements.forEach((name, supplier) -> json.add(name, supplier.get()));

		Path path = output.getOutputFolder().resolve("assets/minecraft/atlases/blocks.json");
		return DataProvider.saveStable(cache, json.get("atlas"), path);
	}

	@Override
	public String getName() {
		return "Atlas Data Generator";
	}
}
