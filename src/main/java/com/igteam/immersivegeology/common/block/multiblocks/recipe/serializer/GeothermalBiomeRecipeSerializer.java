/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEStreamCodecs;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalBiomeRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

public class GeothermalBiomeRecipeSerializer extends IERecipeSerializer<GeothermalBiomeRecipe> {
	public static final MapCodec<GeothermalBiomeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			RegistryFixedCodec.create(Registries.BIOME).optionalFieldOf("biome").forGetter(r -> r.biomes.leftOptional()),
			TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("biome_tags").forGetter(r -> r.biomes.rightOptional()),
			Codec.INT.fieldOf("min_heat").forGetter(GeothermalBiomeRecipe::getMinHeat),
			Codec.INT.fieldOf("max_heat").forGetter(GeothermalBiomeRecipe::getMaxHeat)
	).apply(inst, (biome, tags, min_heat, max_heat) -> {
		Preconditions.checkArgument(biome.isPresent()!=tags.isPresent(),
				"GeothermalBiomeRecipe must have exactly one of 'biome' or 'biome_tags'.");
		if(biome.isPresent())
			return new GeothermalBiomeRecipe(biome.get(), min_heat, max_heat);
		return new GeothermalBiomeRecipe(tags.orElseThrow(), min_heat, max_heat);
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeothermalBiomeRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.either(
					ByteBufCodecs.holderRegistry(Registries.BIOME),
					IEStreamCodecs.tagCodec(Registries.BIOME).apply(ByteBufCodecs.list())
			), r -> r.biomes.toDFU(),
			ByteBufCodecs.INT, GeothermalBiomeRecipe::getMinHeat,
			ByteBufCodecs.INT, GeothermalBiomeRecipe::getMaxHeat,
			(biomes, min_heat, max_heat) -> biomes.map(
					biome -> new GeothermalBiomeRecipe(biome, min_heat, max_heat),
					tags -> new GeothermalBiomeRecipe(tags, min_heat, max_heat)
			)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, GeothermalBiomeRecipe> CODECS = new DualMapCodec<>(CODEC, STREAM_CODEC);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, GeothermalBiomeRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}
}
