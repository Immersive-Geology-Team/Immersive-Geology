/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CrystallizerRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GravitySeparatorRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class IGRecipes extends RecipeProvider
{
	public IGRecipes(PackOutput pOutput)
	{
		super(pOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer)
	{
		multiblockRecipes(consumer);
	}

	private void multiblockRecipes(Consumer<FinishedRecipe> consumer)
	{
		GravitySeparatorRecipeBuilder.builder(MineralEnum.Acanthite.getItemTag(ItemCategoryFlags.CRUSHED_ORE)).setChance(0.5f).setByproduct(Items.GRAVEL).setTime(100).setWater(100).addInput(MineralEnum.Acanthite.getItemTag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).build(consumer, new ResourceLocation(IGLib.MODID, "gravityseparator/test_recipe"));
		CrystallizerRecipeBuilder.builder(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.CRYSTAL), 1).setEnergy(1000).setTime(100).addInput(FluidTags.WATER, 1000).build(consumer, new ResourceLocation(IGLib.MODID, "crystallizer/test_recipe"));
	}
}
