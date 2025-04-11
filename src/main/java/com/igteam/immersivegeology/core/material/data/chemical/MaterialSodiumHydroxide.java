/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSodiumHydroxide extends MaterialChemical
{
	public MaterialSodiumHydroxide()
	{
		super(MetalEnum.Unobtanium, MineralEnum.Bauxite);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xffe3ce77));
	}

	@Override
	public void setupRecipeStages()
	{
		//	public IGCrystallizationMethod create(String name, ItemStack output, FluidStack fluid_out, TagKey<Fluid> fluidTag, int fluidAmount, int time, int energy)
		IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create("sodium_hydroxide_to_metal",
				MetalEnum.Sodium.getStack(ItemCategoryFlags.CRYSTAL),
				new FluidStack(Fluids.WATER, IGLib.ACID_RECOVERED_FROM_SLURRY),
				getFluidTag(), IGLib.SLURRY_TO_CRYSTAL_MB,
				300, 38400);
	}
}
