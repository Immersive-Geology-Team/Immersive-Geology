/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.BiFunction;

public class MaterialBindingAgent extends MaterialChemical
{
	public MaterialBindingAgent() {
		super();
		removeMaterialFlags(BlockCategoryFlags.SLURRY);
	}

	@Override
	public void setupRecipeStages()
	{
		logged_recipes.add(getName());
		//		TODO FIX This shit
		//		IGMethodBuilder.mixing(this, IGStageDesignation.SYNTHESIS).create(Items.CLAY_BALL, FluidTags.WATER, 4000, 4000);
		//		IGMethodBuilder.mixing(this, IGStageDesignation.SYNTHESIS).create(IGRegistrationHolder.getItem.apply("raw_fire_clay"), FluidTags.WATER, 6000, 6000);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0x826548));
	}
}
