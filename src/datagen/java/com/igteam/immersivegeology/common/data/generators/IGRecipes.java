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
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.builders.*;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDecoration;
import blusunrize.immersiveengineering.common.register.IEBlocks.StoneDecoration;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import blusunrize.immersiveengineering.common.register.IEItems.Misc;
import blusunrize.immersiveengineering.common.register.IEItems.Molds;
import blusunrize.immersiveengineering.data.tags.IEItemTags;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.*;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.metal.MaterialAluminum;
import com.igteam.immersivegeology.core.material.data.mineral.MaterialAnthracite;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.data.types.MaterialRadioactiveMetal;
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
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.util.Metal;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
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
				.save(consumer, IGLib.MODID+"ig_item_repair");

		Item bronze_ingot = MetalEnum.Bronze.getItem(ItemCategoryFlags.INGOT);

		// Bronze Hammer
		Item toolkit_0 = MetalEnum.Bronze.getItem(ItemCategoryFlags.HAMMER);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_0)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', bronze_ingot).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(bronze_ingot)).save(consumer, ig("craft_igtoolkit_0"));

		Item unobtanium_hoe = MetalEnum.Unobtanium.getItem(ItemCategoryFlags.TOOL_HOE);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, unobtanium_hoe)
				.pattern("uu")
				.pattern(" s")
				.pattern(" s")
				.define('u', MetalEnum.Unobtanium.getItemTag(ItemCategoryFlags.INGOT))
				.define('s', Tags.Items.RODS_WOODEN)
				.group("ig_tools")
				.unlockedBy("has_unobtanium_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Unobtanium.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_unobtanium_hoe"));

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
				.pattern(" W ").define('F', Items.FLINT).define('B', Ingredient.of(Tags.Items.COBBLESTONE)).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COBBLESTONE)).save(consumer, ig("craft_geologist_pick"));

		Item steel_geologist_pick = IGRegistrationHolder.getItem.apply("prospector_kit_steel");
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, steel_geologist_pick)
				.pattern("FBF")
				.pattern("SWS")
				.pattern(" W ").define('F', MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.PLATE)).define('B', MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.INGOT)).define('W', MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.ROD)).define('S', MetalEnum.Steel.getItem(ItemCategoryFlags.WIRE))
				.group("ig_tools").unlockedBy("has_stainless_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_steel_geologist_pick"));


		// Stone Hammer
		Item toolkit_2 = StoneEnum.MCStone.getItem(ItemCategoryFlags.HAMMER);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, toolkit_2)
				.pattern(" BS")
				.pattern(" WB")
				.pattern("W  ").define('B', Ingredient.of(Tags.Items.COBBLESTONE)).define('W', Ingredient.of(Tags.Items.RODS_WOODEN)).define('S', Ingredient.of(Tags.Items.STRING))
				.group("ig_tools").unlockedBy("has_stone", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COBBLESTONE)).save(consumer, ig("craft_igtoolkit_2"));

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

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MetalEnum.TungstenCarbide.getItem(ItemCategoryFlags.POWDER))
				.requires(MetalEnum.Tungsten.getItem(ItemCategoryFlags.POWDER))
				.requires(Ingredients.DUST_HOP_GRAPHITE)
				.unlockedBy("has_tungsten_powder", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Tungsten.getItem(ItemCategoryFlags.GRIT)))
				.save(consumer, ig("craft_tungsten_carbide_powder"));

		RotaryKilnRecipeBuilder.builder(MetalEnum.TungstenCarbide.getItem(ItemCategoryFlags.INGOT))
				.addInput(MetalEnum.TungstenCarbide.getItemTag(ItemCategoryFlags.POWDER)).setTime(1200).setHeat(RotaryKilnLogic.EHV_HEAT_CAP)
				.build(consumer, new ResourceLocation("calcination/synthesis_tungstencarbide"));

		Item ehv_cable = MiscEnum.Cable.getBlock(BlockCategoryFlags.ENERGY_PIPE).asItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ehv_cable)
				.pattern("RAR")
				.pattern("ACA")
				.pattern("RAR")
				.define('C', IEBlocks.Metals.STORAGE.get(EnumMetals.ELECTRUM))
				.define('R', MiscEnum.EHVInsulation.getItem(ItemCategoryFlags.PLATE))
				.define('A', MetalEnum.TungstenCarbide.getItem(ItemCategoryFlags.WIRE))
				.unlockedBy("has_hv_coil", InventoryChangeTrigger.TriggerInstance.hasItems(MetalDecoration.HV_COIL))
				.save(consumer, ig("craft_ehv_cable"));

		BottlingMachineRecipeBuilder.builder(MiscEnum.EHVInsulation.getItem(ItemCategoryFlags.PLATE))
				.addResult(Molds.MOLD_PLATE).addInput(new ItemLike[]{Molds.MOLD_PLATE}).addFluidTag(MiscEnum.EHVInsulation.getFluidTag(), 250).build(consumer, new ResourceLocation(IGLib.MODID, "bottling/duroplast_plate"));

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
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, raw_fire_clay, 4)
				.requires(Items.CLAY_BALL, 2)
				.requires(Items.FLINT)
				.requires(Ingredient.of(ItemTags.SAND), 2)
				.group("raw_fire_clay").unlockedBy("has_clay", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLAY_BALL)).save(consumer, ig("craft_raw_fire_clay"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(raw_fire_clay), RecipeCategory.MISC, refractory_brick, 0, 120).group("refractory_brick_cooking").unlockedBy("has_raw_refractory_brick", InventoryChangeTrigger.TriggerInstance.hasItems(raw_fire_clay)).save(consumer, ig("cook_refractory_brick"));
		// Titanium Reinforced Concrete
		Block trconcrete = MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK);
		Item titanium_ingot = MetalEnum.Titanium.getItem(ItemCategoryFlags.INGOT);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, trconcrete, 9)
				.pattern("PWP")
				.pattern("RCR")
				.pattern("PWP")
				.define('P', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.PLATE)))
				.define('W', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.WIRE)))
				.define('R', Ingredient.of(MetalEnum.Titanium.getItemTag(ItemCategoryFlags.ROD)))
				.define('C', Ingredient.of(IEFluids.CONCRETE.getBucket())).group("ig_engineering").unlockedBy("has_titanium", InventoryChangeTrigger.TriggerInstance.hasItems(titanium_ingot)).save(consumer, ig("craft_titanium_concrete"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.SLAB), 6).define('i', MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("iii").unlockedBy("has_sheetmetal_" + MiscEnum.TitaniumConcrete.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK))).save(consumer, ig("block_to_slab_" + MiscEnum.TitaniumConcrete.getName().toLowerCase()));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STAIRS), 4).define('i', MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("i  ").pattern("ii ").pattern("iii").unlockedBy("has_sheetmetal_" + MiscEnum.TitaniumConcrete.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(MiscEnum.TitaniumConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK))).save(consumer, ig("block_to_stair_" + MiscEnum.TitaniumConcrete.getName().toLowerCase()));

		Block srconcrete = MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK);
		Item steel_ingot = MetalEnum.Steel.getItem(ItemCategoryFlags.INGOT);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, srconcrete, 9)
				.pattern("PWP")
				.pattern("RCR")
				.pattern("PWP")
				.define('P', Ingredient.of(MetalEnum.Steel.getItemTag(ItemCategoryFlags.PLATE)))
				.define('W', Ingredient.of(MetalEnum.Steel.getItemTag(ItemCategoryFlags.WIRE)))
				.define('R', Ingredient.of(MetalEnum.Steel.getItemTag(ItemCategoryFlags.ROD)))
				.define('C', Ingredient.of(IEFluids.CONCRETE.getBucket())).group("ig_engineering").unlockedBy("has_steel", InventoryChangeTrigger.TriggerInstance.hasItems(steel_ingot)).save(consumer, ig("craft_reinforced_concrete"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.SLAB), 6).define('i', MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("iii").unlockedBy("has_sheetmetal_" + MiscEnum.ReinforceConcrete.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK))).save(consumer, ig("block_to_slab_" + MiscEnum.ReinforceConcrete.getName().toLowerCase()));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STAIRS), 4).define('i', MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("i  ").pattern("ii ").pattern("iii").unlockedBy("has_sheetmetal_" + MiscEnum.ReinforceConcrete.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(MiscEnum.ReinforceConcrete.getBlock(BlockCategoryFlags.STORAGE_BLOCK))).save(consumer, ig("block_to_stair_" + MiscEnum.ReinforceConcrete.getName().toLowerCase()));


		Block bituminous_coal_block = MineralEnum.Bituminous.getBlock(BlockCategoryFlags.STORAGE_BLOCK);
		Item bituminous_coal = MineralEnum.Bituminous.getItem(ItemCategoryFlags.NORMAL_ORE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bituminous_coal_block, 1).define('i', bituminous_coal).pattern("iii").pattern("iii").pattern("iii").unlockedBy("has_coal_" + MineralEnum.Bituminous.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(bituminous_coal)).save(consumer, ig("coal_to_block_" + MineralEnum.Bituminous.getName().toLowerCase()));


		Block lignite_coal_block = MineralEnum.Lignite.getBlock(BlockCategoryFlags.STORAGE_BLOCK);
		Item lignite_coal = MineralEnum.Lignite.getItem(ItemCategoryFlags.NORMAL_ORE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, lignite_coal_block, 1).define('i', lignite_coal).pattern("iii").pattern("iii").pattern("iii").unlockedBy("has_coal_" + MineralEnum.Lignite.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(lignite_coal)).save(consumer, ig("coal_to_block_" + MineralEnum.Lignite.getName().toLowerCase()));

		Block anthracite_coal_block = MineralEnum.Anthracite.getBlock(BlockCategoryFlags.STORAGE_BLOCK);
		Item anthracite_coal = MineralEnum.Anthracite.getItem(ItemCategoryFlags.NORMAL_ORE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, anthracite_coal_block, 1).define('i', anthracite_coal).pattern("iii").pattern("iii").pattern("iii").unlockedBy("has_coal_" + MineralEnum.Anthracite.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(anthracite_coal)).save(consumer, "coal_to_block_" + MineralEnum.Anthracite.getName().toLowerCase());
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, anthracite_coal, 9).requires(Ingredient.of(new ItemStack(anthracite_coal_block)), 1).unlockedBy("has_anthracite_block", InventoryChangeTrigger.TriggerInstance.hasItems(anthracite_coal_block)).save(consumer, ig("anthracite_from_storage_block"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, bituminous_coal, 9).requires(Ingredient.of(new ItemStack(bituminous_coal_block)), 1).unlockedBy("has_bituminous_block", InventoryChangeTrigger.TriggerInstance.hasItems(bituminous_coal_block)).save(consumer, ig("bituminous_from_storage_block"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, lignite_coal, 9).requires(Ingredient.of(new ItemStack(lignite_coal_block)), 1).unlockedBy("has_lignite_block", InventoryChangeTrigger.TriggerInstance.hasItems(lignite_coal_block)).save(consumer, ig("lignite_from_storage_block"));

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

		Item tungsten_component = MetalEnum.Tungsten.getStack(ItemCategoryFlags.MECHANICAL_COMPONENT).getItem();
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tungsten_component)
				.pattern("P P")
				.pattern(" I ")
				.pattern("P P")
				.define('P', Ingredient.of(MetalEnum.Tungsten.getItemTag(ItemCategoryFlags.PLATE)))
				.define('I', Ingredient.of(IETags.getTagsFor(EnumMetals.COPPER).ingot)).group("ig_tungsten_component").unlockedBy("has_tungsten", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Tungsten.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_tungsten_component"));


		for(MetalEnum metal : MetalEnum.values())
		{
			if(metal.hasFlag(ItemCategoryFlags.PLATE) && metal.hasFlag(BlockCategoryFlags.CRATE))
			{
				ShapedRecipeBuilder.shaped(RecipeCategory.MISC, metal.getBlock(BlockCategoryFlags.CRATE)).define('p', metal.getItem(ItemCategoryFlags.PLATE)).pattern("ppp").pattern("p p").pattern("ppp").unlockedBy("has_plate_" + metal.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(metal.getItem(ItemCategoryFlags.PLATE))).save(consumer, ig("get_crate_from_" + metal.getName() + "_plates"));
			}
			if(metal.hasFlag(ItemCategoryFlags.NUGGET) && metal.hasFlag(ItemCategoryFlags.INGOT))
			{
				ShapedRecipeBuilder.shaped(RecipeCategory.MISC, metal.getItem(ItemCategoryFlags.INGOT),1).define('n', metal.getItem(ItemCategoryFlags.NUGGET)).pattern("nnn").pattern("nnn").pattern("nnn").unlockedBy("has_nugget_"+metal.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(metal.getItem(ItemCategoryFlags.NUGGET))).save(consumer, ig("get_ingot_from_"+metal.getName()+"_nuggets"));
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, metal.getItem(ItemCategoryFlags.NUGGET), 9).requires(metal.getItem(ItemCategoryFlags.INGOT)).unlockedBy("has_ingot_"+metal.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(metal.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("get_nuggets_from_"+metal.getName()+"_ingot"));
			}
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
			if(metal.hasFlag(BlockCategoryFlags.FENCE))
			{
				assert(metal.hasFlag(ItemCategoryFlags.ROD));
				assert(metal.hasFlag(ItemCategoryFlags.INGOT));
				ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, metal.getBlock(BlockCategoryFlags.FENCE), 3).pattern("iri").pattern("iri").define('i', metal.getItem(ItemCategoryFlags.INGOT)).define('r', metal.getItem(ItemCategoryFlags.ROD)).unlockedBy("has_rod_and_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(metal.getItem(ItemCategoryFlags.ROD), metal.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_"+metal.getName()+"_fence"));
			}

			if(metal.hasFlag(ItemCategoryFlags.DRILL_HEAD) && metal.hasFlag(BlockCategoryFlags.STORAGE_BLOCK) && metal.hasFlag(ItemCategoryFlags.INGOT))
			{
				ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, metal.getItem(ItemCategoryFlags.DRILL_HEAD)).unlockedBy("has_ingot_" + metal.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(metal.getItem(ItemCategoryFlags.INGOT)))
						.pattern("  I")
						.pattern("II ")
						.pattern("BI ")
						.define('I', metal.getItem(ItemCategoryFlags.INGOT))
						.define('B', metal.getBlock(BlockCategoryFlags.STORAGE_BLOCK))
						.save(consumer, ig("drill_head_" + metal.getName()));
			}
			if(metal.hasFlag(BlockCategoryFlags.STORAGE_BLOCK))
			{
				if(metal.instance() instanceof MaterialRadioactiveMetal m)
				{
					ThermoelectricSourceBuilder.builder(metal.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).kelvin(m.heatValue()).build(consumer, IGLib.rl("thermoelectric/"+metal.getName()));
				}
			}
		}

		GeothermalExchangerRecipeBuilder.builder(new FluidStack(MiscEnum.Steam.getFluid(BlockCategoryFlags.FLUID), 50)).addInput(FluidTags.WATER, 25).setTime(20).setEnergy(2560).build(consumer, IGLib.rl("geothermal/water_to_steam"));
	}

	private static final List<MetalEnum> plates_and_rods_to_register = List.of(
			MetalEnum.HighSpeedSteel, MetalEnum.Thorium, MetalEnum.Titanium, MetalEnum.Hastelloy, MetalEnum.Unobtanium,
			MetalEnum.Vanadium, MetalEnum.Zirconium, MetalEnum.TungstenCarbide, MetalEnum.Manganese, MetalEnum.Chromium,
			MetalEnum.Magnesium, MetalEnum.Molybdenum, MetalEnum.StainlessSteel, MetalEnum.Neodymium);

	private static final List<MetalEnum> wires_to_register = List.of(MetalEnum.Neodymium, MetalEnum.Titanium, MetalEnum.TungstenCarbide);

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

		GeothermalConversionRecipeBuilder.builder(Blocks.LAVA, 1100, null, Pair.of(Blocks.MAGMA_BLOCK, 900)).build(consumer, new ResourceLocation(IGLib.MODID, "geoconvert/lava"));
		GeothermalConversionRecipeBuilder.builder(Blocks.MAGMA_BLOCK, 900, Pair.of(Blocks.LAVA, 1173), Pair.of(Blocks.OBSIDIAN, 300)).build(consumer, new ResourceLocation(IGLib.MODID, "geoconvert/magma"));
		GeothermalConversionRecipeBuilder.builder(Blocks.OBSIDIAN, 300, Pair.of(Blocks.MAGMA_BLOCK, 920), null).build(consumer, new ResourceLocation(IGLib.MODID, "geoconvert/obsidian"));
		TurbineFuelBuilder.builder(MiscEnum.Steam.getFluidTag(BlockCategoryFlags.FLUID), 0.5f, 21).build(consumer, new ResourceLocation(IGLib.MODID, "turbine_fuel/steam"));

		for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
		{
			if(material.hasFlag(ItemCategoryFlags.CRUSHED_ORE) && material.hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE)) {
				for(ItemCategoryFlags ore : List.of(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE))
				{
					if(!material.hasFlag(ore)) continue;
					float chance = 0.33f;
					int nerfed_amount = ore.equals(ItemCategoryFlags.POOR_ORE) ? 1 : (ore.equals(ItemCategoryFlags.NORMAL_ORE) ? 2 : 3);
					int time = 100;
					int energy = 6000;
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
				if(material.hasFlag(ItemCategoryFlags.INGOT) && material.hasFlag(BlockCategoryFlags.STORAGE_BLOCK) && !material.instance().hasExistingFlag(BlockCategoryFlags.STORAGE_BLOCK))
				{
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.STORAGE_BLOCK), 1).define('i', material.getItem(ItemCategoryFlags.INGOT)).pattern("iii").pattern("iii").pattern("iii").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.INGOT))).save(consumer, "ingot_to_block_" + material.getName().toLowerCase());
					ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getItem(ItemCategoryFlags.INGOT), 9).requires(material.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).unlockedBy(material.getName()+"has_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.STORAGE_BLOCK))).save(consumer, ig(material.getName()+"_get_ingots_from_block"));

					if(material.hasFlag(BlockCategoryFlags.STAIRS) && !material.instance().hasExistingFlag(BlockCategoryFlags.STAIRS))
					{
						ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.STAIRS), 4).define('i', material.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("i  ").pattern("ii ").pattern("iii").unlockedBy("has_storage_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("storage_to_stair_" + material.getName().toLowerCase()));
					}
					if(material.hasFlag(BlockCategoryFlags.SLAB) && !material.instance().hasExistingFlag(BlockCategoryFlags.SLAB))
					{
						ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SLAB), 6).define('i', material.getBlock(BlockCategoryFlags.STORAGE_BLOCK)).pattern("iii").unlockedBy("has_storage_"+material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("storage_to_slab_"+material.getName().toLowerCase()));
					}
				}
				if(material.hasFlag(BlockCategoryFlags.SHEETMETAL_BLOCK) && material.hasFlag(ItemCategoryFlags.PLATE) && !material.instance().checkExistingImplementation(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.PLATE))
				{
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK), 4).define('i', material.getItem(ItemCategoryFlags.PLATE)).pattern(" i ").pattern("i i").pattern(" i ").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.PLATE))).save(consumer, ig("plate_to_sheetmetal_" + material.getName().toLowerCase()));
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_SLAB), 6).define('i', material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK)).pattern("iii").unlockedBy("has_sheetmetal_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("sheetmetal_to_slab_" + material.getName().toLowerCase()));
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getBlock(BlockCategoryFlags.SHEETMETAL_STAIRS), 4).define('i', material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK)).pattern("i  ").pattern("ii ").pattern("iii").unlockedBy("has_sheetmetal_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK))).save(consumer, ig("sheetmetal_to_stair_" + material.getName().toLowerCase()));
				}

				if(material.hasFlag(BlockCategoryFlags.SCAFFOLDING))
				{
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getDefault()).define('r', material.getItem(ItemCategoryFlags.ROD)).define('i', material.getItem(ItemCategoryFlags.INGOT)).pattern("iii").pattern(" r ").pattern("r r").unlockedBy("has_ingot_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(material.getItem(ItemCategoryFlags.INGOT))).save(consumer, ig("craft_scaffolding_" + material.getName().toLowerCase()));

					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getGrate()).requires(((MetalEnum)material).getScaffoldingBlock().getDefault()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getDefault())).save(consumer, ig("craft_scaffolding_grated_" + material.getName().toLowerCase()));
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getWoodenTop()).requires(((MetalEnum)material).getScaffoldingBlock().getGrate()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getGrate())).save(consumer, ig("craft_scaffolding_wood_top_" + material.getName().toLowerCase()));
					ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ((MetalEnum)material).getScaffoldingBlock().getDefault()).requires(((MetalEnum)material).getScaffoldingBlock().getWoodenTop()).unlockedBy("has_scaffolding_" + material.getName(), InventoryChangeTrigger.TriggerInstance.hasItems(((MetalEnum)material).getScaffoldingBlock().getWoodenTop())).save(consumer, ig("craft_scaffolding_default_" + material.getName().toLowerCase()));
				}
			}
		}

		BlueprintCraftingRecipeBuilder.builder("components", MetalEnum.Hastelloy.getStack(ItemCategoryFlags.MECHANICAL_COMPONENT))
				.addInput(new IngredientWithSize(MetalEnum.Hastelloy.getItemTag(ItemCategoryFlags.PLATE), 2))
				.addInput(new IngredientWithSize(IETags.getTagsFor(EnumMetals.ELECTRUM).ingot))
				.build(consumer, new ResourceLocation(IGLib.MODID, "blueprint/component_hastelloy"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MetalEnum.Hastelloy.getBlock(BlockCategoryFlags.ENGINEERING_BLOCK), 4)
				.unlockedBy("has_hastelloy_component", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Hastelloy.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)))
						.define('s', MetalEnum.Hastelloy.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).asItem()).define('c', MetalEnum.Hastelloy.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)).define('o', MetalEnum.Silver.getItem(ItemCategoryFlags.INGOT))
						.pattern("scs").pattern("coc").pattern("scs").save(consumer, ig("craft_chemical_engineering_block"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, MetalEnum.Tungsten.getBlock(BlockCategoryFlags.ENGINEERING_BLOCK), 4)
				.unlockedBy("has_tungsten_component", InventoryChangeTrigger.TriggerInstance.hasItems(MetalEnum.Tungsten.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)))
				.define('s', MetalEnum.Tungsten.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).asItem()).define('c', MetalEnum.Tungsten.getItem(ItemCategoryFlags.MECHANICAL_COMPONENT)).define('o', Metals.INGOTS.get(EnumMetals.STEEL).asItem())
				.pattern("scs").pattern("coc").pattern("scs").save(consumer, ig("craft_thermal_engineering_block"));


		// Register bloomery fuels
		BloomeryFuelBuilder.builder(Items.CHARCOAL).setTime(BASE_CHARCOAL_TIME).build(consumer, IGLib.rl("bloomery/bloomery_fuel_charcoal"));
		BloomeryFuelBuilder.builder(Ingredients.COAL_COKE).setTime(BASE_COAL_COKE_TIME).build(consumer, IGLib.rl("bloomery/bloomery_fuel_coke"));
		BloomeryFuelBuilder.builder(Items.COAL).setTime(BASE_COAL_TIME).build(consumer, IGLib.rl("bloomery/bloomery_fuel_coal"));

		CokeOvenRecipeBuilder.builder(Ingredients.COAL_COKE.asItem()).setOil(500).addInput(MineralEnum.Lignite.getItem(ItemCategoryFlags.INGOT))
				.setTime(1800).build(consumer, new ResourceLocation(IGLib.MODID, "coking/lignite_brick_to_coal_coke"));

		// Helper method to register mineral fuels for bloomery with different qualities
		registerMineralBloomeryFuels(consumer, MineralEnum.Lignite, BASE_LIGNITE_TIME);
		registerMineralBloomeryFuels(consumer, MineralEnum.Bituminous, BASE_BITUMINOUS_TIME);
		registerMineralBloomeryFuels(consumer, MineralEnum.Anthracite, BASE_ANTHRACITE_TIME);

		// Register blast furnace fuels for anthracite
		registerBlastFurnaceFuels(consumer, MineralEnum.Anthracite, BASE_ANTHRACITE_TIME);

		// Register torch recipes
		registerTorchRecipes(consumer, MineralEnum.Lignite, 0.5f);
		registerTorchRecipes(consumer, MineralEnum.Bituminous, 1.0f);
		registerTorchRecipes(consumer, MineralEnum.Anthracite, 2.0f);

		// Register coking recipes for bituminous coal
		registerCokingRecipes(consumer, MineralEnum.Bituminous);
	}

	final int POOR_QUALITY_MULTIPLIER = 1;
	final int NORMAL_QUALITY_MULTIPLIER = 2;
	final int RICH_QUALITY_MULTIPLIER = 3;

	// Base burn times for different fuel types
	final int BASE_COAL_TIME = 500;
	final int BASE_LIGNITE_TIME = 150;
	final int BASE_BITUMINOUS_TIME = 500;
	final int BASE_ANTHRACITE_TIME = 600;
	final int BASE_CHARCOAL_TIME = 1200;
	final int BASE_COAL_COKE_TIME = 1400;

	// Helper method to register bloomery fuels for different mineral qualities
	private void registerMineralBloomeryFuels(Consumer<FinishedRecipe> consumer, MineralEnum mineral, int baseTime) {
		String mineralName = mineral.getName();
		BloomeryFuelBuilder.builder(mineral.getItem(ItemCategoryFlags.NORMAL_ORE))
				.setTime(baseTime * NORMAL_QUALITY_MULTIPLIER)
				.build(consumer, IGLib.rl("bloomery/bloomery_fuel_" + mineralName + "_normal"));

		BloomeryFuelBuilder.builder(mineral.getBlock(BlockCategoryFlags.STORAGE_BLOCK))
				.setTime((baseTime * NORMAL_QUALITY_MULTIPLIER) * 10)
				.build(consumer, IGLib.rl("bloomery/bloomery_fuel_block_" + mineralName + "_normal"));
	}

	// Helper method to register blast furnace fuels
	private void registerBlastFurnaceFuels(Consumer<FinishedRecipe> consumer, MineralEnum mineral, int baseTime) {
		String mineralName = mineral.getName();
		BlastFurnaceFuelBuilder.builder(mineral.getItem(ItemCategoryFlags.NORMAL_ORE))
				.setTime(baseTime * NORMAL_QUALITY_MULTIPLIER)
				.build(consumer, IGLib.rl("blastfuel/normal_" + mineralName));

		BlastFurnaceFuelBuilder.builder(mineral.getBlock(BlockCategoryFlags.STORAGE_BLOCK))
				.setTime((baseTime * NORMAL_QUALITY_MULTIPLIER) * 10)
				.build(consumer, IGLib.rl("blastfuel/normal_block_" + mineralName));
	}

	// Helper method to register torch recipes
	private void registerTorchRecipes(Consumer<FinishedRecipe> consumer, MineralEnum mineral, float mult) {
		String mineralName = mineral.getName();
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.TORCH, Mth.floor(4*mult))
				.define('s', Items.STICK)
				.define('c', mineral.getItem(ItemCategoryFlags.NORMAL_ORE))
				.pattern("c")
				.pattern("s")
				.unlockedBy("has_" + mineralName + "_normal", InventoryChangeTrigger.TriggerInstance.hasItems(mineral.getItem(ItemCategoryFlags.NORMAL_ORE)))
				.save(consumer, ig("torch_from_normal_" + mineralName));
	}

	// Helper method to register coking recipes
	private void registerCokingRecipes(Consumer<FinishedRecipe> consumer, MineralEnum mineral) {
		String mineralName = mineral.getName();
		CokeOvenRecipeBuilder.builder(IETags.coalCoke, 1)
				.setOil(500)
				.addInput(mineral.getItem(ItemCategoryFlags.NORMAL_ORE))
				.setTime(1800)
				.build(consumer, new ResourceLocation(IGLib.MODID, "coking/normal_" + mineralName + "_to_coke"));

		CokeOvenRecipeBuilder.builder(IETags.getItemTag(IETags.coalCokeBlock), 1)
				.addInput(mineral.getBlock(BlockCategoryFlags.STORAGE_BLOCK))
				.setOil(5000).setTime(16200)
				.build(consumer, new ResourceLocation(IGLib.MODID, "coking/normal_block_" + mineralName + "_to_coke"));
	}

	private ResourceLocation ig(String crafting)
	{
		return new ResourceLocation(IGLib.MODID, "crafting/" + crafting);
	}
}
