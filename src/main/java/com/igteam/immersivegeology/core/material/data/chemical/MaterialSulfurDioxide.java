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
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
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
		addFlags(MaterialFlags.IS_GAS);
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		IGMethodBuilder.synthesis(this, IGStageDesignation.SYNTHESIS)
				.create("sulfuric_acid_from_water_and_sulfur_dioxide",
						FluidTags.WATER, 2, getFluidTag(), 50,
						Metals.PLATES.get(EnumMetals.LEAD).asItem(),
						ChemicalEnum.SulfuricAcid.getFluidStack(6));

		IGMethodBuilder.synthesis(this, IGStageDesignation.SYNTHESIS)
				.create("sulfuric_acid_from_water_and_sulfur_dioxide",
						FluidTags.WATER, 2, getFluidTag(), 25,
						MetalEnum.Vanadium.getItem(ItemCategoryFlags.PLATE),
						ChemicalEnum.SulfuricAcid.getFluidStack(8));
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xff444444));
	}
}
