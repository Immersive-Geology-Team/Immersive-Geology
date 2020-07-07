package com.igteam.immersivegeology.common.util;

import com.google.gson.stream.JsonWriter;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase.EnumStoneType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.igteam.immersivegeology.ImmersiveGeology.GENERATE_MODELS;

public class ItemJsonGenerator {
	// NOTE: this generate runs at start up, minecraft looks for item and block
	// models BEFORE this runs,
	// which means you need to start minecraft up to generate the json, then close
	// and start minecraft again to see it in game.
	public static void generateDefaultItem(Material material, MaterialUseType type) {
		generateDefaultItem("item_" + type.getName() + "_" + material.getName(), type.getName());
	}

	public static void generateDefaultItem(Material material, MaterialUseType type, EnumStoneType rockType) {
		generateDefaultItem("item_" + type.getName() + "_" + material.getName(),
				type.getName() + "_" + rockType.getName());
	}

	public static void generateDefaultBlockItem(Material material, MaterialUseType type, EnumStoneType rockType) {
		generateBlockItem("block_" + type.getName() + "_" + material.getName(),
				type.getName() + "_" + rockType.getName());
	}

	public static void generateDefaultBlockItem(Material material, MaterialUseType type) {
		generateBlockItem("block_" + type.getName() + "_" + material.getName(), type.getName());
	}
	
	public static void generateOreBearingBlockItem(Material material, MaterialUseType type, EnumStoneType stoneType) {
		generateBlockItem("block_" + type.getName() + "_" + material.getName(),
				type.getName() + "_" + stoneType.getName());
	}


	// NOTE: this generate runs at start up, minecraft looks for item and block
	// models BEFORE this runs,
	// which means you need to start minecraft up to generate the json, then close
	// and start minecraft again to see it in game.
	public static void generateDefaultItem(String registryName, String itemBaseType) {
		if (!GENERATE_MODELS)
			return;
		try {
			File file = new File("../src/main/resources/assets/immersivegeology/models/item/" + registryName + ".json");
			if (!file.exists()) {
				JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
				jsonWriter.setIndent(" "); // this makes it more readable for humans!
				jsonWriter.beginObject();
				jsonWriter.name("parent").value("immersivegeology:item/base/" + itemBaseType);
				jsonWriter.endObject();
				jsonWriter.close();
			}
		} catch (IOException e) {

		}

	}

	public static void generateBlockItem(String registryName, String type) {
		if (!GENERATE_MODELS)
			return;
		try {
			File file = new File("../src/main/resources/assets/immersivegeology/models/item/" + registryName + ".json");
			if (!file.exists()) {
				JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
				jsonWriter.setIndent(" "); // this makes it more readable for humans!
				jsonWriter.beginObject();
				jsonWriter.name("parent").value("immersivegeology:block/base/" + type);
				jsonWriter.endObject();
				jsonWriter.close();
			}
		} catch (IOException e) {

		}
	}

	public static void generateOreBearingBlockItem(String registryName, String type) {
		if (!GENERATE_MODELS)
			return;
		try {
			File file = new File("../src/main/resources/assets/immersivegeology/models/item/" + registryName + ".json");
			if (!file.exists()) {
				JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
				jsonWriter.setIndent(" "); // this makes it more readable for humans!
				jsonWriter.beginObject();
				jsonWriter.name("parent").value("immersivegeology:block/base/ore_bearing/" + type);
				jsonWriter.endObject();
				jsonWriter.close();
			}
		} catch (IOException e) {

		}
	}
}
