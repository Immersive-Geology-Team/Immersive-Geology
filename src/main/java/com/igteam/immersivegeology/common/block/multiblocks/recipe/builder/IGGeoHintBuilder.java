/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.common.recipe.IGGeoRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import java.util.Locale;

public class IGGeoHintBuilder extends IGMultiblockRecipeBuilder<IGGeoHintBuilder>
{
	private final GeologyMaterial material;

	private IGGeoHintBuilder(GeologyMaterial material)
	{
		this.material = material;
	}

	public static IGGeoHintBuilder builder(GeologyMaterial material)
	{
		return new IGGeoHintBuilder(material);
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		out.accept(name, new IGGeoRecipe(material), null, getConditions());
	}
}
