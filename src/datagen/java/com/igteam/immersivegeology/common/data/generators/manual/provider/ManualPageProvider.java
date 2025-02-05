/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.manual.provider;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.data.generators.manual.helper.IGManualType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Objects;

public class ManualPageProvider {
	protected ResourceLocation location;
	protected HashMap<String, ManualPageAnchor> root;

	public ManualPageProvider(ResourceLocation location) {
		this.location = location;
		root = new HashMap<>();
	}

	public ResourceLocation getLocation() {
		assertExistence();
		return location;
	}

	public JsonElement toJson() {
		JsonObject wrapped = new JsonObject();

		for(String key : root.keySet()) {
			wrapped.add(key, root.get(key).toJsonElement());
		}

		return wrapped;
	}

	public void assertExistence() {
		Preconditions.checkState(exists(), "Page at %s does not exist", location);
	}

	protected boolean exists() {
		return true;
	}



	public ManualPageAnchor startAnchor(String s) {
		ManualPageAnchor anchor = new ManualPageAnchor(this, s);
		root.put(s, anchor);
		return anchor;
	}

	public class ManualPageAnchor {
		final String id;
		protected JsonObject root;
		protected IGManualType page_type;
		protected ManualPageProvider provider;
		public ManualPageAnchor(ManualPageProvider provider, String id){
			this.id = id;
			this.root = new JsonObject();
			this.provider = provider;
		}

		public ManualPageAnchor setType(IGManualType type) {
			this.page_type = type;
			root.addProperty("type", type.get());
			return this;
		}

		public ManualPageAnchor addListElements(String key, ResourceLocation... entries) {
			JsonArray jsonArray = new JsonArray();

			for (ResourceLocation entry : entries) {
				JsonObject object = new JsonObject();
				object.addProperty(key.substring(0, key.length()-1), Objects.requireNonNull(entry).toString());
				jsonArray.add(object);
			}

			root.add(key, jsonArray);
			return this;
		}

		public ManualPageProvider closeAnchor(){
			return provider;
		}

		public JsonElement toJsonElement() {
			return root;
		}

		public ManualPageAnchor addElement(ResourceLocation location) {
			root.addProperty("recipe", location.toString());
			return this;
		}
	}
}