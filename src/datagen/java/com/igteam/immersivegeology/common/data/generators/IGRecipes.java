/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.client.helper.TFCCollapseRecipeBuilder;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CrystallizerRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GravitySeparatorRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.RevFurnaceRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

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
		tfcCompatRecipes(consumer);
	}

	private void tfcCompatRecipes(Consumer<FinishedRecipe> consumer)
	{
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IGOreBlock oreBlock)
			{
				TFCCollapseRecipeBuilder.builder(Ingredient.of(oreBlock)).copyInput(true).build(consumer, new ResourceLocation(IGLib.MODID, "collapse/" + block.getId().getPath()));
			}
		}
	}

	private void multiblockRecipes(Consumer<FinishedRecipe> consumer)
	{
		for(MineralEnum mineral : MineralEnum.values())
		{
			if(mineral.hasFlag(ItemCategoryFlags.CRUSHED_ORE) && mineral.hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)) GravitySeparatorRecipeBuilder.builder(mineral.getItemTag(ItemCategoryFlags.CRUSHED_ORE)).setChance(0.5f).setByproduct(Items.GRAVEL).setTime(100).setWater(100).addInput(mineral.getItemTag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).build(consumer, new ResourceLocation(IGLib.MODID, "gravityseparator/dirty_crushed_"+ mineral.getName() + "_to_crushed"));
		}

		RevFurnaceRecipeBuilder.builder(MetalEnum.Unobtanium.getStack(ItemCategoryFlags.INGOT)).setTime(100).setWasteAmount(250).addInput(MineralEnum.Unobtania.getStack(ItemCategoryFlags.CRUSHED_ORE)).build(consumer, new ResourceLocation(IGLib.MODID, "reverberation_furnace/test_recipe"));
		CrystallizerRecipeBuilder.builder(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.CRYSTAL), 1).setEnergy(1000).setTime(100).addInput(FluidTags.WATER, 1000).build(consumer, new ResourceLocation(IGLib.MODID, "crystallizer/test_recipe"));
	}
}
