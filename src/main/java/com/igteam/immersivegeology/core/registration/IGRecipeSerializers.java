/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GravitySeparatorRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.CrystallizerRecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.GravitySeparatorRecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer.RevFurnaceRecipeSerializer;
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
	}

}
