/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.crafting.serializers.SimpleRecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GeothermalBiomeRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.TurbineFuelBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.*;
import com.igteam.immersivegeology.common.recipe.IGGeoRecipe;
import com.igteam.immersivegeology.common.recipe.IGGeoSerializer;
import com.igteam.immersivegeology.common.recipe.IGRepairItemRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.helper.EmptyRecipe;
import com.igteam.immersivegeology.core.registration.helper.EmptySerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class IGRecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGLib.MODID);

	public static final RegistryObject<IERecipeSerializer<EmptyRecipe>> EMPTY_SERIALIZER;
	public static final RegistryObject<SimpleRecipeSerializer<IGRepairItemRecipe>> IG_REPAIR_SERIALIZER;


	static {
		CrystallizerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("crystallizer", CrystallizerRecipeSerializer::new);
		GravitySeparatorRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("gravity_separator", GravitySeparatorRecipeSerializer::new);
		RevFurnaceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("reverberation_furnace", RevFurnaceRecipeSerializer::new);
		RotaryKilnRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("rotary_kiln", RotaryKilnRecipeSerializer::new);
		ChemicalRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("chemical_reactor", ChemicalRecipeSerializer::new);
		CentrifugeRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("centrifuge", CentrifugeRecipeSerializer::new);
		IndustrialSluiceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("industrial_sluice", IndustrialSluiceRecipeSerializer::new);
		BloomeryRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bloomery", BloomeryRecipeSerializer::new);
		BloomeryFuel.SERIALIZER = RECIPE_SERIALIZERS.register("bloomery_fuel", BloomeryFuelSerializer::new);
		TurbineFuel.SERIALIZER = RECIPE_SERIALIZERS.register("tubrine_fuel", TurbineFuelSerializer::new);
		BallmillRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("ballmill", BallmillRecipeSerializer::new);
		PelletizerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("pelletizer", PelletizerRecipeSerializer::new);
		CoreDrillRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("coredrill", CoreDrillSerializer::new);
		FoundryRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("foundry", FoundryRecipeSerializer::new);
		GeothermalExchangerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("geothermal_exchanger", GeothermalExchangerRecipeSerializer::new);
		GeothermalConversionRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("geothermal_conversion", GeothermalConversionRecipeSerializer::new);
		GeothermalBiomeRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("geothermal_biome", GeothermalBiomeRecipeSerializer::new);
		IGGeoRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("geo_hint", IGGeoSerializer::new);

		IG_REPAIR_SERIALIZER = RECIPE_SERIALIZERS.register("ig_item_repair", special(IGRepairItemRecipe::new));

		EMPTY_SERIALIZER = RECIPE_SERIALIZERS.register("empty", EmptySerializer::new);
	}

	private static <T extends Recipe<?>> Supplier<SimpleRecipeSerializer<T>> special(Function<ResourceLocation, T> create)
	{
		return () -> new SimpleRecipeSerializer<>(create);
	}
}
