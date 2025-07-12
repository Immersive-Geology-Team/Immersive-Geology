/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.HazardTypes;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import java.util.Set;
import java.util.function.BiFunction;

public class MaterialHydrogenSulfide extends MaterialChemical
{

	public MaterialHydrogenSulfide()
	{
		super();
		removeMaterialFlags(BlockCategoryFlags.SLURRY, BlockCategoryFlags.CLOUDY_SLURRY, MaterialFlags.IS_CHEMICAL);
		addFlags(MaterialFlags.IS_GAS, BlockCategoryFlags.HYDROVENT);
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		if(flag.equals(BlockCategoryFlags.HYDROVENT)) return new ResourceLocation(IGLib.MODID, "block/greyscale/stone/cobble");
		return super.getTextureLocation(flag);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
		return ((p, i) -> (0xffaaaaaa));
	}

	@Override
	public Set<HazardTypes> getHazards()
	{
		return Set.of(HazardTypes.TOXIC, HazardTypes.CORROSIVE, HazardTypes.FLAMMABLE);
	}
}
