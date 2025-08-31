/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.utils.FastEither;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeothermalBiomeRecipe extends IESerializableRecipe
{
	public static RegistryObject<IERecipeSerializer<GeothermalBiomeRecipe>> SERIALIZER;
	public static final CachedRecipeList<GeothermalBiomeRecipe> RECIPES;
	public final FastEither<Biome, List<TagKey<Biome>>> biomes;
	private final int min_heat;
	private final int max_heat;

	public GeothermalBiomeRecipe(ResourceLocation id, Biome biome, int min_heat, int max_heat) {
		super(LAZY_EMPTY, IGRecipeTypes.GEOTHERMAL_EXCHANGER_BIOME, id);
		this.biomes = FastEither.left(biome);
		this.min_heat = min_heat;
		this.max_heat = max_heat;
	}

	public GeothermalBiomeRecipe(ResourceLocation id, List<TagKey<Biome>> biomes, int min_heat, int max_heat) {
		super(LAZY_EMPTY, IGRecipeTypes.GEOTHERMAL_EXCHANGER_BIOME, id);
		this.biomes = FastEither.right(biomes);
		this.min_heat = min_heat;
		this.max_heat = max_heat;
	}

	public List<TagKey<Biome>> getBiomes() {
		return this.biomes.map(
				biome -> {
					Optional<Holder<Biome>> holderOpt = ForgeRegistries.BIOMES.getHolder(biome);
					return holderOpt.map(biomeHolder -> biomeHolder.getTagKeys()
							.collect(Collectors.toList())).orElse(Collections.emptyList());
				},
				Function.identity()
		);
	}

	public static GeothermalBiomeRecipe findRecipe(Level level, TagKey<Biome> biome)
	{
		for(GeothermalBiomeRecipe recipe : RECIPES.getRecipes(level))
			if(recipe.getBiomes().contains(biome))
				return recipe;
		return null;
	}

	public int getMinHeat() {
		return min_heat;
	}

	public int getMaxHeat() {
		return max_heat;
	}

	protected IERecipeSerializer<?> getIESerializer() {
		return SERIALIZER.get();
	}

	@Nonnull
	public ItemStack getResultItem(@NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	static {
		RECIPES = new CachedRecipeList<>(IGRecipeTypes.GEOTHERMAL_EXCHANGER_BIOME);
	}
}
