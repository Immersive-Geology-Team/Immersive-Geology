/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.*;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class IGRecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGLib.MODID);

	static {
		CrystallizerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("crystallizer", CrystallizerRecipeSerializer::new);
		GravitySeparatorRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("gravityseparator", GravitySeparatorRecipeSerializer::new);
		RevFurnaceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("reverberation_furnace", RevFurnaceRecipeSerializer::new);
		RotaryKilnRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("rotarykiln", RotaryKilnRecipeSerializer::new);
		ChemicalRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("chemical_reactor", ChemicalRecipeSerializer::new);;
		CentrifugeRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("centrifuge", CentrifugeRecipeSerializer::new);
		IndustrialSluiceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("industrial_sluice", IndustrialSluiceRecipeSerializer::new);
	}

}
