/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.ScaffoldingHelper;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class MaterialRuined extends MaterialMisc
{
	public MaterialRuined()
	{
		super();
		this.name = "rusty_metal";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.DEFAULT_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS,
				BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SHEETMETAL_SLAB, BlockCategoryFlags.SHEETMETAL_STAIRS);
		addFlags(BlockCategoryFlags.SCAFFOLDING, ItemCategoryFlags.PLATE, BlockCategoryFlags.ENGINEERING_BLOCK, BlockCategoryFlags.FENCE,
				BlockCategoryFlags.ADVANCED_ENGINEERING_BLOCK, BlockCategoryFlags.CRATE);

		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();

		IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION)
				.create("crude_steel_extraction", this.getItemTag(ItemCategoryFlags.PLATE), new ItemStack(Metals.NUGGETS.get(EnumMetals.STEEL), 2));


		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("plate_extraction", this.getItemTag(ItemCategoryFlags.PLATE), 1, new ItemStack(Metals.NUGGETS.get(EnumMetals.STEEL), 4), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("light_engineering_extraction", this.getBlock(BlockCategoryFlags.ENGINEERING_BLOCK).asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.IRON), 2), new ItemStack(Metals.INGOTS.get(EnumMetals.COPPER)))
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("heavy_engineering_extraction", this.getBlock(BlockCategoryFlags.ADVANCED_ENGINEERING_BLOCK).asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.STEEL), 2), new ItemStack(Metals.INGOTS.get(EnumMetals.ELECTRUM)))
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("scaffolding_extraction", this.getScaffoldingBlock().getDefault().asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.IRON), 1), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);
		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("grate_scaffolding_extraction", this.getScaffoldingBlock().getGrate().asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.IRON), 1), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);
		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("top_scaffolding_extraction", this.getScaffoldingBlock().getWoodenTop().asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.IRON), 1), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("steel_extraction", this.getBlock(BlockCategoryFlags.SHEETMETAL_BLOCK).asItem(), 1, new ItemStack(Metals.NUGGETS.get(EnumMetals.IRON), 6), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("storage_extraction", this.getBlock(BlockCategoryFlags.STORAGE_BLOCK).asItem(), 1, new ItemStack(Metals.INGOTS.get(EnumMetals.STEEL), 4), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("panel_extraction", this.getBlock(BlockCategoryFlags.DEFAULT_BLOCK).asItem(), 1, new ItemStack(Metals.PLATES.get(EnumMetals.STEEL), 4), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("stair_extraction", this.getBlock(BlockCategoryFlags.STAIRS).asItem(), 1, new ItemStack(Metals.INGOTS.get(EnumMetals.STEEL), 2), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("sheetmetal_stair_extraction", this.getBlock(BlockCategoryFlags.SHEETMETAL_STAIRS).asItem(), 1, new ItemStack(Metals.NUGGETS.get(EnumMetals.STEEL), 4), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("slab_extraction", this.getBlock(BlockCategoryFlags.SLAB).asItem(), 1, new ItemStack(Metals.INGOTS.get(EnumMetals.STEEL), 1), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("sheetmetal_slab_extraction", this.getBlock(BlockCategoryFlags.SHEETMETAL_SLAB).asItem(), 1, new ItemStack(Metals.NUGGETS.get(EnumMetals.STEEL), 3), ItemStack.EMPTY)
				.setTimeAndEnergy(100, 25600);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION)
				.create("fence_extraction", this.getBlock(BlockCategoryFlags.FENCE).asItem(), 1, new ItemStack(Ingredients.STICK_STEEL, 2), new ItemStack(Metals.INGOTS.get(EnumMetals.STEEL), 1))
				.setTimeAndEnergy(100, 25600);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		if(flag instanceof BlockCategoryFlags blockFlag)
		{
			return switch(blockFlag)
			{
				case STORAGE_BLOCK, SLAB, STAIRS -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/block");
				case ENGINEERING_BLOCK -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/light");
				case ADVANCED_ENGINEERING_BLOCK -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/heavy");
				case DEFAULT_BLOCK -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/deco");
				case CRATE -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/crate");
				case SCAFFOLDING -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/scaffolding/scaffolding");
				case SHEETMETAL_BLOCK, SHEETMETAL_SLAB, SHEETMETAL_STAIRS -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/sheetmetal");
				default -> super.getTextureLocation(flag);
			};
		}
		if(flag instanceof ItemCategoryFlags itemFlag)
		{
			return switch(itemFlag)
			{
				case PLATE -> new ResourceLocation(IGLib.MODID, "item/colored/rusty_metal/scrap");
				case GRIT -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/scaffolding/scaffolding");
				default -> super.getTextureLocation(flag);
			};
		}
		return new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/deco");
	}

	@Override
	public Properties getProperties(IFlagType<?> flag)
	{
		return super.getProperties(flag).sound(SoundType.COPPER);
	}
}
