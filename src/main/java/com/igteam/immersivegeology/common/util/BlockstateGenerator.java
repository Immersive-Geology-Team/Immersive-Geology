package com.igteam.immersivegeology.common.util;

import com.google.gson.stream.JsonWriter;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.igteam.immersivegeology.ImmersiveGeology.GENERATE_MODELS;

public class BlockstateGenerator
{
	//NOTE: this generate runs at start up, minecraft looks for item and block models BEFORE this runs, 
	//which means you need to start minecraft up to generate the json, then close and start minecraft again to see it in game.
	public static void generateDefaultBlock(Material material, MaterialUseType type)
	{
		generateDefaultBlock("block_"+type.getName()+"_"+material.getName(), material.getMaterialType().toString(), type.getName());
	}

	//NOTE: this generate runs at start up, minecraft looks for item and block models BEFORE this runs, 
	//which means you need to start minecraft up to generate the json, then close and start minecraft again to see it in game.
	public static void generateDefaultBlock(String registryName, String itemType, String itemBaseType)
	{
		if(!GENERATE_MODELS)
			return;
		try
		{
			File file = new File("../src/main/resources/assets/immersivegeology/blockstates/"+registryName+".json");
			if(!file.exists())
			{
				JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
				jsonWriter.setIndent(" "); //this makes it more readable for humans!
				jsonWriter.beginObject();
				jsonWriter.name("variants");
				jsonWriter.beginObject();
				jsonWriter.name("");
				jsonWriter.beginObject();
				jsonWriter.name("model");
				jsonWriter.value("immersivegeology:block/base/"+itemBaseType);
				jsonWriter.endObject();
				jsonWriter.endObject();
				jsonWriter.endObject();
				jsonWriter.close();
			}
		} catch(IOException e)
		{

		}

	}

}
