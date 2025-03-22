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
import com.igteam.immersivegeology.client.helper.IGVeinTextureType;

import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

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
		List<String> vein_types = Arrays.stream(IGVeinTextureType.values()).map(IGVeinTextureType::getSanitizedName).toList();
		for(String  vein_type : vein_types)
		{
			for(OreRichness richness : OreRichness.values())
			{
				textures.add("palette/block/ore_bearing/" + vein_type + "/" + richness.name().toLowerCase() + "_1");
				textures.add("palette/block/ore_bearing/" + vein_type + "/" + richness.name().toLowerCase() + "_2");
			}
		}
		for(ItemCategoryFlags flag : ItemCategoryFlags.values())
		{
			if(flag.hasPalette() &! flag.equals(ItemCategoryFlags.DRILL_HEAD))
				for(int variation = 1; variation <= flag.getVariations(); variation++) textures.add("palette/item/" + flag.getName() + "/type_" + variation);
			if(flag.hasPalette() && flag.equals(ItemCategoryFlags.DRILL_HEAD))
			{
				textures.add("palette/item/" + flag.getName() + "/drill");
				textures.add("palette/item/" + flag.getName() + "/drillhead");
			}
		}
		List<String> scaffolding_textures = new ArrayList<>();
		scaffolding_textures.add("block/greyscale/scaffolding/scaffolding");
		scaffolding_textures.add("block/greyscale/scaffolding/scaffolding_top_grate_top");
		scaffolding_textures.add("block/greyscale/scaffolding/scaffolding_top_wooden_top");


		IGPaletteKey key = new IGPaletteKey(new ResourceLocation(IGLib.MODID, "palette/palette_key"), new ResourceLocation(IGLib.MODID, "palette/palettes"));
		addDirectorySource(sourcesArray,"item", "item/");
		key.addTextureData(textures);
		key.addTextureData(scaffolding_textures);
		key.addEntryData(MetalEnum.getAtlasPermutations());
		key.addEntryData(MineralEnum.getAtlasPermutations());
		sourcesArray.add(key.getJsonObj());
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
		return "Block Atlas Data Generator";
	}

	static class IGPaletteKey
	{
		private ResourceLocation key, paletteLoc;
		private List<String> textures, entries;
		JsonObject obj;
		public IGPaletteKey(ResourceLocation key, ResourceLocation paletteLoc)
		{
			this.key = key;
			this.paletteLoc = paletteLoc;
			this.textures = new ArrayList<>();
			this.entries = new ArrayList<>();
		}

		public void addTextureData(Collection<String> textureData)
		{
			this.textures.addAll(textureData);
		}

		public void addEntryData(Collection<String> entryData)
		{
			this.entries.addAll(entryData);
		}

		private void makeJson()
		{
			this.obj = new JsonObject();
			obj.addProperty("type", "paletted_permutations");

			JsonArray texturesArray = new JsonArray();
			textures.forEach(t -> texturesArray.add(IGLib.MODID + ":" + t));

			obj.add("textures", texturesArray);

			obj.addProperty("palette_key", key.toString());

			JsonObject permutationsObj = new JsonObject();
			for (String item : entries) {
				if(item.equalsIgnoreCase("kaolinite")) continue;
				permutationsObj.addProperty(item.toLowerCase().substring(item.lastIndexOf('/')+1) + "_" + item.substring(0,item.lastIndexOf('/')), paletteLoc.toString() + "/" + item.toLowerCase());
			}
			obj.add("permutations", permutationsObj);
		}

		JsonObject getJsonObj()
		{
			if(obj == null) makeJson();
			return obj;
		}
	}

}
