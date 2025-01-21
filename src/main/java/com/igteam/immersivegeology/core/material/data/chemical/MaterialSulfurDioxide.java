/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSulfurDioxide extends MaterialChemical
{

	public MaterialSulfurDioxide()
	{
		super();
		removeMaterialFlags(BlockCategoryFlags.SLURRY, MaterialFlags.IS_CHEMICAL);
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		IGMethodBuilder.synthesis(this, IGStageDesignation.SYNTHESIS).create("sulfuric_acid_from_water_and_sulfur_dioxide", FluidTags.WATER, 8, getFluidTag(), 8, Metals.PLATES.get(EnumMetals.LEAD).asItem(), ChemicalEnum.SulfuricAcid.getFluidStack(16));
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0x444444));
	}
}
