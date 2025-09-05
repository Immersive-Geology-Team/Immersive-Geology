/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalBiomeRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GeothermalBiomeRecipeSerializer extends IERecipeSerializer<GeothermalBiomeRecipe> {

	@Override
	public ItemStack getIcon() {
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}

	@Override
	public GeothermalBiomeRecipe readFromJson(ResourceLocation id, JsonObject json, IContext context) {
		if (json.has("biome") && json.has("biome_tags")) {
			throw new JsonParseException("GeothermalBiomeRecipe must have either 'biome' or 'biome_tags', not both.");
		}

		if (json.has("biome")) {
			ResourceLocation biomeId = new ResourceLocation(json.get("biome").getAsString());
			Biome biome = ForgeRegistries.BIOMES.getValue(biomeId);
			Preconditions.checkNotNull(biome, "Biome '%s' not found", biomeId);
			int min_heat = json.get("min_heat").getAsInt();
			int max_heat = json.get("max_heat").getAsInt();
			return new GeothermalBiomeRecipe(id, biome, min_heat, max_heat);
		}

		if (json.has("biome_tags")) {
			JsonArray tagArray = json.getAsJsonArray("biome_tags");
			List<TagKey<Biome>> tags = new ArrayList<>();
			for (JsonElement el : tagArray) {
				ResourceLocation tagId = new ResourceLocation(el.getAsString());
				tags.add(TagKey.create(Registries.BIOME, tagId));
			}
			int min_heat = json.get("min_heat").getAsInt();
			int max_heat = json.get("max_heat").getAsInt();
			return new GeothermalBiomeRecipe(id, tags,  min_heat, max_heat);
		}

		throw new JsonParseException("Missing required field 'biome' or 'biome_tags' in GeothermalBiomeRecipe.");
	}

	@Override
	public @Nullable GeothermalBiomeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
		boolean isSingleBiome = buffer.readBoolean();

		if (isSingleBiome) {
			Biome biome = buffer.readRegistryIdUnsafe(ForgeRegistries.BIOMES);
			int min_heat = buffer.readInt();
			int max_heat = buffer.readInt();
			return new GeothermalBiomeRecipe(id, biome, min_heat, max_heat);
		} else {
			int count = buffer.readVarInt();
			List<TagKey<Biome>> tags = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ResourceLocation tagId = buffer.readResourceLocation();
				tags.add(TagKey.create(Registries.BIOME, tagId));
			}
			int min_heat = buffer.readInt();
			int max_heat = buffer.readInt();
			return new GeothermalBiomeRecipe(id, tags, min_heat, max_heat);
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GeothermalBiomeRecipe recipe) {
		List<TagKey<Biome>> tags = recipe.getBiomes();

		if (recipe.biomes.isLeft()) {
			buffer.writeBoolean(true); // indicates single biome
			Biome biome = recipe.biomes.leftNonnull();
			buffer.writeRegistryIdUnsafe(ForgeRegistries.BIOMES, biome);
			buffer.writeInt(recipe.getMinHeat());
			buffer.writeInt(recipe.getMaxHeat());
		} else {
			buffer.writeBoolean(false); // indicates list of tags
			buffer.writeVarInt(tags.size());
			for (TagKey<Biome> tag : tags) {
				buffer.writeResourceLocation(tag.location());
			}
			buffer.writeInt(recipe.getMinHeat());
			buffer.writeInt(recipe.getMaxHeat());
		}
	}
}
