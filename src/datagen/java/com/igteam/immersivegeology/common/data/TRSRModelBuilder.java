/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import blusunrize.immersiveengineering.data.models.TransformationMap;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class TRSRModelBuilder extends ModelBuilder<TRSRModelBuilder>
{
	private final TransformationMap transforms = new TransformationMap();
	private final List<TRSRModelBuilder.SimpleOverride> overrides = new ArrayList();

	public TRSRModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
		super(outputLocation, existingFileHelper);
	}

	public TRSRModelBuilder transforms(ResourceLocation source) {
		try {
			Resource transformFile = this.existingFileHelper.getResource(source, PackType.CLIENT_RESOURCES, ".json", "transformations");
			String jsonString = CharStreams.toString(new InputStreamReader(transformFile.open()));
			this.transforms.addFromJson(jsonString);
			return this;
		} catch (IOException var4) {
			IOException e = var4;
			throw new RuntimeException("While loading transforms from " + source, e);
		}
	}

	public TRSRModelBuilder override(ModelFile model, ResourceLocation predicateKey, float predicateValue) {
		this.overrides.add(new TRSRModelBuilder.SimpleOverride(model, Map.of(predicateKey, predicateValue)));
		return this;
	}

	public JsonObject toJson() {
		JsonObject ret = super.toJson();
		JsonObject transformJson = this.transforms.toJson();
		if (!transformJson.entrySet().isEmpty()) {
			ret.add("display", transformJson);
		}

		if (!this.overrides.isEmpty()) {
			ret.add("overrides", (JsonElement)this.overrides.stream().map(TRSRModelBuilder.SimpleOverride::toJson).collect(Collector.of(JsonArray::new, JsonArray::add, (jsonElements, jsonElements2) -> {
				jsonElements.addAll(jsonElements2);
				return jsonElements;
			})));
		}

		return ret;
	}

	static record SimpleOverride(ModelFile model, Map<ResourceLocation, Float> predicates) {
		SimpleOverride(ModelFile model, Map<ResourceLocation, Float> predicates) {
			this.model = model;
			this.predicates = predicates;
		}

		JsonObject toJson() {
			JsonObject ret = new JsonObject();
			JsonObject predicatesJson = new JsonObject();
			this.predicates.forEach((key, val) -> {
				predicatesJson.addProperty(key.toString(), val);
			});
			ret.add("predicate", predicatesJson);
			ret.addProperty("model", this.model.getLocation().toString());
			return ret;
		}

		public ModelFile model() {
			return this.model;
		}

		public Map<ResourceLocation, Float> predicates() {
			return this.predicates;
		}
	}
}