/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.builders.BlueprintCraftingRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CokeOvenRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.MetalPressRecipeBuilder;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import blusunrize.immersiveengineering.common.register.IEItems.Molds;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BloomeryFuelBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.CoreDrillRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.GravitySeparatorRecipeBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.IndustrialSluiceRecipeBuilder;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
		IGLib.IG_LOGGER.info("Started Registration of Immersive Geology Recipes");
		multiblockRecipes(consumer);
		tfcCompatRecipes(consumer);
		manualRecipes(consumer);
		IGRegistrationHolder.buildMaterialRecipes();
		methodRecipes(consumer);
		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Recipes");
	}

	private void methodRecipes(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("- Method Recipe Registration");
		for(MaterialInterface<?> entry : IGLib.getGeologyMaterials())
		{
			for(IGRecipeStage stage : entry.getStageSet())
			{
				for(IGRecipeMethod recipe_method : stage.getMethods())
				{
					if(!recipe_method.build(consumer)) IGLib.IG_LOGGER.warn("Failed to build Recipe Method [{}] for material [{}]", recipe_method.getMethod().getMethodName(), entry.getName());
				}
			}
		}
	}

	private void manualRecipes(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("- Basic Recipe Registration");


		CoreDrillRecipeBuilder.builder(MetalEnum.MoltenMantle.getFluid(BlockCategoryFlags.FLUID)).addInput(new FluidTagInput(FluidTags.WATER, 1)).build(consumer, new ResourceLocation(IGLib.MODID, "basic_coredrill"));

		SpecialRecipeBuilder.special(IGRecipeSerializers.IG_REPAIR_SERIALIZER.get())
				.save(consumer, IGLib.MODID+":ig_item_repair");

		Item bronze_ingot = MetalEnum.Bronze.getItem(ItemCategoryFlags.INGOT);

		// Bronze Hammer
		Item toolkit_0 = MetalEnum.Bronze.getItem(ItemCategoryFlags.HAMMER);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_0)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', bronze_ingot).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(bronze_ingot)).save(consumer, ig("craft_igtoolkit_0"));

		// Stainless Steel Hammer
		Item toolkit_1 = MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.HAMMER);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_1)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', MetalEnum.StainlessSteel.getItemTag(ItemCategoryFlags.INGOT)).define('W', MetalEnum.StainlessSteel.getItemTag(ItemCategoryFlags.ROD)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_stainless_steel_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_igtoolkit_1"));

		Item geologist_pick = IGRegistrationHolder.getItem.apply("prospector_kit");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, geologist_pick)
				.pattern("SBF")
				.pattern("BW ")
				.pattern(" W ").define('F', Items.FLINT).define('B', Ingredient.of(Tags.Items.COBBLESTONE)).define('W', Ingredient.of(Ingredients.STICK_TREATED)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COBBLESTONE)).save(consumer, ig("craft_geologist_pick"));

		// Stone Hammer
		Item toolkit_2 = StoneEnum.MCStone.getItem(ItemCategoryFlags.HAMMER);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_2)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', Ingredient.of(Tags.Items.COBBLESTONE)).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_stone", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COBBLESTONE)).save(consumer, ig("craft_igtoolkit_2"));

		Item schematic_table = IGRegistrationHolder.getBlock.apply("drawing_table").asItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, schematic_table)
				.pattern("SSS")
				.pattern("C F").define('C', Ingredient.of(Blocks.CRAFTING_TABLE)).define('S', Ingredient.of(ItemTags.WOODEN_SLABS)).define('F', Ingredient.of(Tags.Items.FENCES_WOODEN))
				.group("ig_schematics").unlockedBy("has_crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(consumer, ig("craft_schematic_table"));

		// Refractory Brick Block
		Item refractory_brick = IGRegistrationHolder.getItem.apply("refractory_brick");
		Item refractory = MiscEnum.Refractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, refractory)
				.pattern("BB")
				.pattern("BB").define('B', refractory_brick)
				.group("ig_tools").unlockedBy("has_refractory_brick", InventoryChangeTrigger.TriggerInstance.hasItems(refractory_brick)).save(consumer, ig("craft_refractory_bricks"));

		Item refractory_slab = MiscEnum.Refractory.getStack(BlockCategoryFlags.SLAB).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, refractory_slab, 6)
				.pattern("BBB").define('B', refractory)
				.group("ig_tools").unlockedBy("has_refractory_bricks", InventoryChangeTrigger.TriggerInstance.hasItems(refractory)).save(consumer, ig("craft_refractory_bricks_slab"));

		// Reinforced Refractory Brick Block
		Item reinforced_refractory = MiscEnum.ReinforcedRefractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem();
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, reinforced_refractory)
				.requires(MiscEnum.Refractory.getStack(BlockCategoryFlags.STORAGE_BLOCK).getItem())
				.requires(MetalEnum.Bronze.getItemTag(ItemCategoryFlags.PLATE))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(bronze_ingot)).save(consumer, ig("craft_reinforced_refractory_bricks"));

		Item reinforced_refractory_slab = MiscEnum.ReinforcedRefractory.getStack(BlockCategoryFlags.SLAB).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, reinforced_refractory_slab, 6)
				.pattern("BBB").define('B', reinforced_refractory)
				.group("ig_tools").unlockedBy("has_refractory_bricks", InventoryChangeTrigger.TriggerInstance.hasItems(refractory)).save(consumer, ig("craft_reinforced_refractory_bricks_slab"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, reinforced_refractory_slab, 2)
				.pattern("SBS").define('B', MetalEnum.Bronze.getItemTag(ItemCategoryFlags.PLATE)).define('S', refractory_slab)
				.group("ig_tools").unlockedBy("has_refractory_bricks", InventoryChangeTrigger.TriggerInstance.hasItems(refractory)).save(consumer, ig("craft_reinforced_refractory_bricks_slab_alt"));


		// Bronze Plate
		Item bronze_plate = MetalEnum.Bronze.getItem(ItemCategoryFlags.PLATE);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, bronze_plate)
				.requires(MetalEnum.Bronze.getItemTag(ItemCategoryFlags.INGOT))
				.requires(toolkit_0)
				.group("ig_plate_from_ingot_hammer").unlockedBy("has_bronze_work_hammer", InventoryChangeTrigger.TriggerInstance.hasItems(toolkit_0)).save(consumer, ig("craft_bronze_plate_with_bronze_hammer"));

		Item copper_plate = Metals.PLATES.get(EnumMetals.COPPER).asItem();
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, copper_plate)
				.requires(Items.COPPER_INGOT)
				.requires(toolkit_0)
				.group("ig_plate_from_ingot_hammer").unlockedBy("has_bronze_work_hammer", InventoryChangeTrigger.TriggerInstance.hasItems(toolkit_0)).save(consumer, ig("craft_copper_plate_with_bronze_hammer"));


		Item raw_fire_clay = IGRegistrationHolder.getItem.apply("raw_fire_clay");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, raw_fire_clay, 2)
				.requires(Items.CLAY_BALL)
				.requires(Items.FLINT)
				.requires(ItemTags.SAND)
				.group("raw_fire_clay").unlockedBy("has_clay", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLAY_BALL)).save(consumer, ig("craft_raw_fire_clay"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(raw_fire_clay), RecipeCategory.MISC, refractory_brick, 0, 120).group("refractory_brick_cooking").unlockedBy("has_raw_refractory_brick", InventoryChangeTrigger.TriggerInstance.hasItems(raw_fire_clay)).save(consumer, ig("cook_refractory_brick"));
		// Titanium Reinforced Concrete
		Block trconcrete = IGRegistrationHolder.getBlock.apply("trconcrete");
		Item titanium_ingot = MetalEnum.Titanium.getItem(ItemCategoryFlags.INGOT);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, trconcrete, 9)
				.pattern("PWP")
				.pattern("RCR")
				.pattern("PWP")
				.define('P', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.PLATE)))
				.define('W', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.WIRE)))
				.define('R', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.ROD)))
				.define('C', Ingredient.of(IEFluids.CONCRETE.getBucket())).group("ig_engineering").unlockedBy("has_titanium", InventoryChangeTrigger.TriggerInstance.hasItems(titanium_ingot)).save(consumer, ig("craft_titanium_concrete"));

		//Computational Engineering Block
		Item computational_engineering = MetalEnum.StainlessSteel.getBlock(BlockCategoryFlags.ENGINEERING_BLOCK).asItem();
		Item stainlesssteel_ingot = MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.INGOT);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, computational_engineering)
				.pattern("ABA")
				.pattern("CPC")
				.pattern("ABA")
				.define('A', Ingredient.of(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.INGOT)))
				.define('B', Ingredient.of(Ingredients.COMPONENT_ELECTRONIC))
				.define('C', Ingredient.of(Ingredients.COMPONENT_ELECTRONIC_ADV))
				.define('P', Ingredient.of(Ingredients.CIRCUIT_BOARD)).group("ig_engineering").unlockedBy("has_stainlesssteel", InventoryChangeTrigger.TriggerInstance.hasItems(stainlesssteel_ingot)).save(consumer, ig("craft_computational_engineering"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, computational_engineering)
				.pattern("ACA")
				.pattern("BPB")
				.pattern("ACA")
				.define('A', Ingredient.of(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.INGOT)))
				.define('B', Ingredient.of(Ingredients.COMPONENT_ELECTRONIC))
				.define('C', Ingredient.of(Ingredients.COMPONENT_ELECTRONIC_ADV))
				.define('P', Ingredient.of(Ingredients.CIRCUIT_BOARD)).group("ig_engineering").unlockedBy("has_stainlesssteel", InventoryChangeTrigger.TriggerInstance.hasItems(stainlesssteel_ingot)).save(consumer, ig("craft_computational_engineering_2"));

		//Hastelloy Component
		Item hastelloy_component = MetalEnum.Hastelloy.getStack(ItemCategoryFlags.MECHANICAL_COMPONENT).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hastelloy_component)
				.pattern("P P")
				.pattern(" I ")
				.pattern("P P")
				.define('P', Ingredient.of(MetalEnum.Hastelloy.getItemTag(ItemCategoryFlags.PLATE)))
				.define('I', Ingredient.of(IETags.getTagsFor(EnumMetals.ELECTRUM).ingot)).group("ig_hastelloy_component").unlockedBy("has_hastelloy", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Hastelloy.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_hastelloy_component"));

		for(MetalEnum metal : MetalEnum.values())
		{
			if(metal.hasFlag(ItemCategoryFlags.PLATE) && !metal.instance().hasExistingFlag(ItemCategoryFlags.PLATE) && plates_and_rods_to_register.contains(metal))
			{
				MetalPressRecipeBuilder.builder(Molds.MOLD_PLATE, metal.getItemTag(ItemCategoryFlags.PLATE), 1).addInput(metal.getItemTag(ItemCategoryFlags.INGOT)).setEnergy(2400).build(consumer, new ResourceLocation(IGLib.MODID, "metal_press/ingot_to_plate_"+metal.getName()));
			}
			if(metal.hasFlag(ItemCategoryFlags.WIRE) && !metal.instance().hasExistingFlag(ItemCategoryFlags.WIRE) && wires_to_register.contains(metal))
			{
				MetalPressRecipeBuilder.builder(Molds.MOLD_WIRE, metal.getItemTag(ItemCategoryFlags.WIRE), 2).addInput(metal.getItemTag(ItemCategoryFlags.INGOT)).setEnergy(2400).build(consumer, new ResourceLocation(IGLib.MODID, "metal_press/ingot_to_wire_"+metal.getName()));
			}
			if(metal.hasFlag(ItemCategoryFlags.ROD) && !metal.instance().hasExistingFlag(ItemCategoryFlags.ROD) && plates_and_rods_to_register.contains(metal))
			{
				MetalPressRecipeBuilder.builder(Molds.MOLD_ROD, metal.getItemTag(ItemCategoryFlags.ROD), 2).addInput(metal.getItemTag(ItemCategoryFlags.INGOT)).setEnergy(2400).build(consumer, new ResourceLocation(IGLib.MODID, "metal_press/ingot_to_rod_"+metal.getName()));
			}
		}
	}

	private static final List<MetalEnum> plates_and_rods_to_register = List.of(MetalEnum.Thorium, MetalEnum.Titanium, MetalEnum.Hastelloy, MetalEnum.Unobtanium, MetalEnum.Vanadium, MetalEnum.Zirconium, MetalEnum.TungstenCarbide, MetalEnum.Manganese, MetalEnum.Chromium, MetalEnum.Magnesium, MetalEnum.Molybdenum, MetalEnum.StainlessSteel, MetalEnum.Neodymium);
	private static final List<MetalEnum> wires_to_register = List.of(MetalEnum.Neodymium, MetalEnum.Titanium);

	private void tfcCompatRecipes(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("- Terra Firma Craft Recipe Registration");
		if(!ModFlags.TFC.isStrictlyLoaded())
		{
			IGLib.IG_LOGGER.info("- SKIPPED [TFC Not Loaded]");
			return;
		}
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IOreBlock oreBlock)
			{
				if(ModFlags.TFC.isStrictlyLoaded()) TFCDatagenCompat.runRecipeDatagen(oreBlock, consumer, block);
			}
		}
	}

	private void multiblockRecipes(Consumer<FinishedRecipe> consumer)
	{
		IGLib.IG_LOGGER.info("- Multiblock Test Recipe Registration");
		Item stone_work_hammer = StoneEnum.MCStone.getItem(ItemCategoryFlags.HAMMER);
		Item bronze_work_hammer = MetalEnum.Bronze.getItem(ItemCategoryFlags.HAMMER);
		Item stainless_work_hammer = MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.HAMMER);

		for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
		{
			if(material.hasFlag(ItemCategoryFlags.CRUSHED_ORE) && material.hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)) {
				for(ItemCategoryFlags ore : List.of(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE))
				{
					float chance = 0.33f;
					int nerfed_amount = ore.equals(ItemCategoryFlags.POOR_ORE) ? 1 : (ore.equals(ItemCategoryFlags.NORMAL_ORE) ? 2 : 3);
					int time = 100;
					int energy = 100;
					ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getItem(ItemCategoryFlags.DIRTY_CRUSHED_ORE), nerfed_amount).requires(material.getItemTag(ore)).requires(material.getItemTag(ore)).requires(ItemCategoryFlags.HAMMER.getCategoryTag()).unlockedBy("has_work_hammer", InventoryChangeTrigger.TriggerInstance.hasItems(stone_work_hammer)).save(consumer,ig("crush_" + material.getName().toLowerCase() + "_"+ ore.getName().toLowerCase() + "_with_work_hammer"));

					CrusherRecipeBuilder builder = CrusherRecipeBuilder.builder(material.getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1));
					builder.addSecondary(material.getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance);
					if(ore.equals(ItemCategoryFlags.NORMAL_ORE) || ore.equals(ItemCategoryFlags.RICH_ORE)) builder.addSecondary(material.getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance / 2);
					if(ore.equals(ItemCategoryFlags.RICH_ORE)) builder.addSecondary(material.getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance / 2);

					builder.addInput(material.getItemTag(ore)).setTime(time).setEnergy(energy).build(consumer, new ResourceLocation(IGLib.MODID, "crusher/" + material.getName().toLowerCase() + "_" + ore.getName().toLowerCase() + "_to_dirty_crushed"));
				}
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getItem(ItemCategoryFlags.CRUSHED_ORE)).requires(material.getItem(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).requires(material.getItem(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).unlockedBy("has_stone_work_hammer", InventoryChangeTrigger.TriggerInstance.hasItems(stone_work_hammer)).save(consumer, ig("wash_dirty_crushed_" + material.getName().toLowerCase()));
				if(!material.hasFlag(ItemCategoryFlags.PELLET)) GravitySeparatorRecipeBuilder.builder(material.getItemTag(ItemCategoryFlags.CRUSHED_ORE)).setChance(0.5f).setByproduct(Items.GRAVEL).setTime(100).setWater(100).addInput(material.getItemTag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)).build(consumer, new ResourceLocation(IGLib.MODID, "gravityseparator/dirty_crushed_"+ material.getName() + "_to_crushed"));
			}

			if(material instanceof MetalEnum)
			{
				if(material.hasFlag(ItemCategoryFlags.CRYSTAL) && material.hasFlag(ItemCategoryFlags.GRIT) &! (material.instance() instanceof MaterialMetalAlloy))
				{
					IGMethodBuilder.crushing(material.instance(), IGStageDesignation.EXTRACTION).create(material.getName() + "_crystal_to_grit", material.getStack(ItemCategoryFlags.CRYSTAL), material.getStack(ItemCategoryFlags.GRIT, 1), 3000, 200);
				}

				if(material.hasFlag(ItemCategoryFlags.INGOT) && material.hasFlag(BlockCategoryFlags.STORAGE_BLOCK) && !material.instance().hasExistingFlag(BlockCategoryFlags.STORAGE_BLOCK))
				{
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.STORAGE_BLOCK), 1).define('i', material.getItem(ItemCategoryFlags.INGOT)).pattern("iii").pattern("iii").pattern("iii").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.INGOT))).save(consumer, "ingot_to_block_" + material.getName().toLowerCase());
				}
				if(material.hasFlag(BlockCategoryFlags.SHEETMETAL_BLOCK) && material.hasFlag(ItemCategoryFlags.PLATE) && !material.instance().checkExistingImplementation(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.PLATE))
				{

					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK), 4).define('i', material.getItem(ItemCategoryFlags.PLATE)).pattern(" i ").pattern("i i").pattern(" i ").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.PLATE))).save(consumer, ig("plate_to_sheetmetal_" + material.getName().toLowerCase()));
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_SLAB), 6).define('i', material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK)).pattern("iii").unlockedBy("has_sheetmetal_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("sheetmetal_to_slab_" + material.getName().toLowerCase()));
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_STAIRS), 4).define('i', material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK)).pattern("i  ").pattern("ii ").pattern("iii").unlockedBy("has_sheetmetal_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("sheetmetal_to_stair_" + material.getName().toLowerCase()));
				}
				if(material.hasFlag(BlockCategoryFlags.SCAFFOLDING))
				{
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getDefault()).define('r', material.getItem(ItemCategoryFlags.ROD)).define('i', material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK)).pattern("iii").pattern(" r ").pattern("r r").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_scaffolding_" + material.getName().toLowerCase()));

					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getGrate()).requires(((MetalEnum)material).getScaffoldingBlock().getDefault()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getDefault())).save(consumer, ig("craft_scaffolding_grated_" + material.getName().toLowerCase()));
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getWoodenTop()).requires(((MetalEnum)material).getScaffoldingBlock().getGrate()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getGrate())).save(consumer, ig("craft_scaffolding_wood_top_" + material.getName().toLowerCase()));
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getDefault()).requires(((MetalEnum)material).getScaffoldingBlock().getWoodenTop()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getWoodenTop())).save(consumer, ig("craft_scaffolding_default_" + material.getName().toLowerCase()));
				}
			}
		}

		GravitySeparatorRecipeBuilder.builder(ItemStack.EMPTY).setByproduct(Items.FLINT).setChance(0.5f).addInput(Items.GRAVEL).build(consumer, ig("wash/gravel_for_flint"));

		BlueprintCraftingRecipeBuilder.builder("components", MetalEnum.Hastelloy.getStack(ItemCategoryFlags.MECHANICAL_COMPONENT)).addInput(new IngredientWithSize(MetalEnum.Hastelloy.getItemTag(ItemCategoryFlags.PLATE), 2)).addInput(new IngredientWithSize(IETags.getTagsFor(EnumMetals.ELECTRUM).ingot)).build(consumer, new ResourceLocation(IGLib.MODID, "blueprint/component_hastelloy"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MetalEnum.Hastelloy.getBlock(BlockCategoryFlags.ENGINEERING_BLOCK), 4)
				.unlockedBy("has_hastelloy_component", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Hastelloy.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)))
						.define('s', MetalEnum.Hastelloy.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).asItem()).define('c', MetalEnum.Hastelloy.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)).define('o', MetalEnum.Silver.getItem(ItemCategoryFlags.INGOT))
						.pattern("scs").pattern("coc").pattern("scs").save(consumer, new ResourceLocation(IGLib.MODID, "craft_chemical_engineering_block"));

		BloomeryFuelBuilder.builder(Items.CHARCOAL).setTime(1200).build(consumer, IGLib.rl("bloomery/bloomery_fuel_charcoal"));
		BloomeryFuelBuilder.builder(Items.COAL).setTime(500).build(consumer, IGLib.rl("bloomery/bloomery_fuel_coal"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 1).define('s', Items.STICK).define('c', MineralEnum.Lignite.getItem(ItemCategoryFlags.POOR_ORE)).pattern("c").pattern("s").unlockedBy("has_lignite_poor", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Lignite.getItem(ItemCategoryFlags.POOR_ORE))).save(consumer, ig("torch_from_poor_lignite"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 2).define('s', Items.STICK).define('c', MineralEnum.Lignite.getItem(ItemCategoryFlags.NORMAL_ORE)).pattern("c").pattern("s").unlockedBy("has_lignite_normal", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Lignite.getItem(ItemCategoryFlags.NORMAL_ORE))).save(consumer, ig("torch_from_normal_lignite"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 4).define('s', Items.STICK).define('c', MineralEnum.Lignite.getItem(ItemCategoryFlags.RICH_ORE)).pattern("c").pattern("s").unlockedBy("has_lignite_rich", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Lignite.getItem(ItemCategoryFlags.RICH_ORE))).save(consumer, ig("torch_from_rich_lignite"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH).define('s', Items.STICK).define('c', MineralEnum.Bituminous.getItem(ItemCategoryFlags.POOR_ORE)).pattern("c").pattern("s").unlockedBy("has_bituminous_poor", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Bituminous.getItem(ItemCategoryFlags.POOR_ORE))).save(consumer, ig("torch_from_poor_bituminous"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 2).define('s', Items.STICK).define('c', MineralEnum.Bituminous.getItem(ItemCategoryFlags.NORMAL_ORE)).pattern("c").pattern("s").unlockedBy("has_bituminous_normal", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Bituminous.getItem(ItemCategoryFlags.NORMAL_ORE))).save(consumer, ig("torch_from_normal_bituminous"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, 4).define('s', Items.STICK).define('c', MineralEnum.Bituminous.getItem(ItemCategoryFlags.RICH_ORE)).pattern("c").pattern("s").unlockedBy("has_bituminous_rich", InventoryChangeTrigger.TriggerInstance.hasItems(MineralEnum.Bituminous.getItem(ItemCategoryFlags.RICH_ORE))).save(consumer, ig("torch_from_rich_bituminous"));

		CokeOvenRecipeBuilder.builder(IETags.coalCoke, 1).setOil(500).addInput(IngredientWithSize.of(MineralEnum.Bituminous.getStack(ItemCategoryFlags.POOR_ORE, 2))).setTime(1800).build(consumer, new ResourceLocation(IGLib.MODID, "coking/poor_bituminous_to_coke"));
		CokeOvenRecipeBuilder.builder(IETags.coalCoke, 1).setOil(500).addInput(MineralEnum.Bituminous.getItem(ItemCategoryFlags.NORMAL_ORE)).setTime(1800).build(consumer, new ResourceLocation(IGLib.MODID, "coking/normal_bituminous_to_coke"));
		CokeOvenRecipeBuilder.builder(IETags.coalCoke, 2).setOil(800).addInput(MineralEnum.Bituminous.getItem(ItemCategoryFlags.RICH_ORE)).setTime(1400).build(consumer, new ResourceLocation(IGLib.MODID, "coking/rich_bituminous_to_coke"));
	}

	private ResourceLocation ig(String crafting)
	{
		return new ResourceLocation(IGLib.MODID, "crafting/" + crafting);
	}
}
