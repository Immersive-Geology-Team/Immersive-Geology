package com.igteam.immersivegeology.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class ItemJsonGenerator {
	
	
	//NOTE: this generate runs at start up, minecraft looks for item and block models BEFORE this runs, 
	//which means you need to start minecraft up to generate the json, then close and start minecraft again to see it in game.
	public void generateDefaultItem(String registryName, String itemType, String itemBaseType) throws IOException {
		File file = new File("../src/main/resources/assets/immersivegeology/models/item/" + registryName + ".json");
		if(!file.exists()) {
			JsonWriter jsonWriter = new JsonWriter(new FileWriter(file)); 
			jsonWriter.setIndent(" "); //this makes it more readable for humans!
			jsonWriter.beginObject();
				jsonWriter.name("parent").value("immersiveengineering:item/ie_item_base");
				jsonWriter.name("textures");
				jsonWriter.beginObject();
					jsonWriter.name("layer0");
					jsonWriter.value("immersivegeology:item/greyscale/" + itemBaseType.toLowerCase() +"/" + itemType.toLowerCase());
				jsonWriter.endObject();
			jsonWriter.endObject();
		    jsonWriter.close();
		}
	}
	
}
