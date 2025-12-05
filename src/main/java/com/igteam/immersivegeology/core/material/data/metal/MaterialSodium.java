/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSodium extends MaterialMetal
{

	public MaterialSodium()
	{
		super();
		addFlags(ItemCategoryFlags.COMPOUND_DUST);
		removeMaterialFlags(ItemCategoryFlags.WIRE, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.GEAR, ItemCategoryFlags.INGOT);
		removeMaterialFlags(BlockCategoryFlags.SHEETMETAL_BLOCK,BlockCategoryFlags.SHEETMETAL_SLAB, BlockCategoryFlags.SHEETMETAL_STAIRS);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xffd0d5db));
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		IGMethodBuilder.decompose(this, IGStageDesignation.ROASTING).create(
				ItemCategoryFlags.METAL_OXIDE,
				ItemCategoryFlags.COMPOUND_DUST,
				1, 300).setMVHeat();
		IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
				"solution_" + getName() + "_to_compound_dust",
				getStack(ItemCategoryFlags.COMPOUND_DUST, 1), //STACK
				null,null,
				new FluidTagInput(ChemicalEnum.ChemicalWaste.getCloudySlurryTagWith(MetalEnum.Sodium), IGLib.ACID_RECOVERED_FROM_SLURRY),
				new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(), IGLib.ACID_RECOVERED_FROM_SLURRY),
				null, 200, 51200);

		IGMethodBuilder.mixing(this, IGStageDesignation.SYNTHESIS).create(ItemCategoryFlags.METAL_OXIDE,
				FluidTags.WATER, 64, ChemicalEnum.SodiumHydroxide.getFluid(BlockCategoryFlags.FLUID), 64);
	}
}
