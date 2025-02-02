/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialEvaporateMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiFunction;

public class MaterialRocksalt extends MaterialEvaporateMineral
{
	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xF3E9FB));
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		/*
			public void create(String name, ItemStack itemOutput, FluidStack fluidOutput, IngredientWithSize itemIn,
			FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy){

		 */
		IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"solution_to_acid",
				MetalEnum.Sodium.getStack(ItemCategoryFlags.COMPOUND_DUST),
				ChemicalEnum.HydrochloricAcid.getFluidStack(80),
				IngredientWithSize.of(ItemStack.EMPTY),
				new FluidTagInput(ChemicalEnum.Brine.getSlurryTagWith(BlockCategoryFlags.SLURRY, this), 80),
				new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 80),
				null,
				200, 51200);
	}

}
