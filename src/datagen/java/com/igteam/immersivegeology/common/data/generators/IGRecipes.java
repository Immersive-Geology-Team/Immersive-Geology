/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CrystallizerRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GravitySeparatorRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.IndustrialSluiceRecipeBuilder;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IGRecipes extends RecipeProvider
{
	public IGRecipes(PackOutput pOutput)
	{
		super(pOutput);
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer)
	{
		multiblockRecipes(consumer);
		tfcCompatRecipes(consumer);
		itemRecipes(consumer);
		IGRegistrationHolder.buildMaterialRecipes();
		methodRecipes(consumer);
	}

	private void methodRecipes(Consumer<FinishedRecipe> consumer)
	{
		for(MaterialInterface<?> entry : IGLib.getGeologyMaterials())
		{
			IGLib.IG_LOGGER.info("Entry: {}", entry.getName());
			for(IGRecipeStage stage : entry.getStageSet())
			{
				IGLib.IG_LOGGER.info("Stage: {}", stage.getStageName());
				for(IGRecipeMethod recipe_method : stage.getMethods())
				{
					IGLib.IG_LOGGER.info("Building Method {}", recipe_method.getMethod().getMethodName());
					if(!recipe_method.build(consumer)) IGLib.IG_LOGGER.warn("Failed to build Recipe Method [{}] for material [{}]", recipe_method.getMethod().getMethodName(), entry.getName());
				}
			}
		}
	}

	private void itemRecipes(Consumer<FinishedRecipe> consumer)
	{
		// Bronze Hammer
		Item toolkit_0 = IGRegistrationHolder.getItem.apply("ig_toolkit_0");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_0)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', MetalEnum.Bronze.getItemTag(ItemCategoryFlags.INGOT)).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK)).save(consumer, "craft_igtoolkit_0");
		// Steel Hammer
		Item toolkit_1 = IGRegistrationHolder.getItem.apply("ig_toolkit_1");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_1)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', MetalEnum.Steel.getItemTag(ItemCategoryFlags.INGOT)).define('W', Ingredient.of(IETags.treatedStick)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_steel_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK)).save(consumer, "craft_igtoolkit_1");
		// Stone Hammer
		Item toolkit_2 = IGRegistrationHolder.getItem.apply("ig_toolkit_2");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_2)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', Ingredient.of(Tags.Items.COBBLESTONE)).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK)).save(consumer, "craft_igtoolkit_2");
		// Refractory Brick Block
		Item refractory = MiscEnum.Refractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, refractory)
				.pattern("BB")
				.pattern("BB").define('B', Ingredient.of(MiscEnum.Refractory.getItemTag(ItemCategoryFlags.INGOT)))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK)).save(consumer, "craft_refractory_bricks");
		// Reinforced Refractory Brick Block
		Item reinforced_refractory = MiscEnum.ReinforcedRefractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem();
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, reinforced_refractory)
				.requires(MiscEnum.Refractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem())
				.requires(MetalEnum.Bronze.getItemTag(ItemCategoryFlags.PLATE))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK)).save(consumer, "craft_reinforced_refractory_bricks");
	}

	private void tfcCompatRecipes(Consumer<FinishedRecipe> consumer)
	{
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IGOreBlock oreBlock)
			{
				if(ModFlags.TFC.isStrictlyLoaded()) TFCDatagenCompat.runRecipeDatagen(oreBlock, consumer, block);
			}
		}
	}

	private void multiblockRecipes(Consumer<FinishedRecipe> consumer)
	{
		for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
		{
			if(material.hasFlag(ItemCategoryFlags.CRUSHED_ORE) && material.hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)) {
				for(ItemCategoryFlags ore : List.of(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE))
				{
					int amount = ore.equals(ItemCategoryFlags.POOR_ORE) ? 2 : (ore.equals(ItemCategoryFlags.NORMAL_ORE) ? 3 : 5);
					int time = 100;
					int energy = 100;
					CrusherRecipeBuilder.builder(material.getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, amount)).addInput(material.getItemTag(ore)).setTime(time).setEnergy(energy).build(consumer, new ResourceLocation(IGLib.MODID, "crusher/" + material.getName().toLowerCase() + "_" + ore.getName().toLowerCase() + "_to_dirty_crushed"));
				}
				GravitySeparatorRecipeBuilder.builder(material.getItemTag(ItemCategoryFlags.CRUSHED_ORE)).setChance(0.5f).setByproduct(Items.GRAVEL).setTime(100).setWater(100).addInput(material.getItemTag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).build(consumer, new ResourceLocation(IGLib.MODID, "gravityseparator/dirty_crushed_"+ material.getName() + "_to_crushed"));
			}
			if(material instanceof MetalEnum)
			{
				if(material.hasFlag(ItemCategoryFlags.CRYSTAL) && material.hasFlag(ItemCategoryFlags.DUST) &! (material.instance() instanceof MaterialMetalAlloy))
				{
					IGMethodBuilder.crushing(material.instance(), IGStageDesignation.EXTRACTION).create(material.getName() + "_crystal_to_dust", material.getItemTag(ItemCategoryFlags.CRYSTAL), material.getStack(ItemCategoryFlags.DUST, 1), 3000, 200);
				}
			}
		}
		NonNullList<StackWithChance> list = NonNullList.create();
		list.add(0, new StackWithChance(MetalEnum.Gold.getStack(ItemCategoryFlags.DUST, 1), 0.4f));
		list.add(1, new StackWithChance(MetalEnum.Silver.getStack(ItemCategoryFlags.DUST, 1), 0.22f));
		list.add(2, new StackWithChance(MetalEnum.Chromium.getStack(ItemCategoryFlags.DUST, 1), 0.321f));
		list.add(3, new StackWithChance(MetalEnum.Copper.getStack(ItemCategoryFlags.DUST, 1), 0.121f));

		IndustrialSluiceRecipeBuilder.builder(MetalEnum.Gold.getStack(ItemCategoryFlags.CRUSHED_ORE)).addInput(MetalEnum.Gold.getItemTag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).setByproducts(list).setTime(100).setWater(100).build(consumer, new ResourceLocation(IGLib.MODID, "sluice/test_recipe"));
	}
}
