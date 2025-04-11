/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.fluids.IEFluid;
import blusunrize.immersiveengineering.common.register.IEFluids;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MaterialEHVInsulation extends MaterialMisc
{
	public MaterialEHVInsulation()
	{
		super();
		this.name = "ehv_insulation";
		addFlags(ItemCategoryFlags.PLATE, BlockCategoryFlags.FLUID);
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
		this.colorFunction = (i,p) -> 0xffD7C8A7;
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();

		//String name, ItemStack itemOutput, FluidStack fluidOutput, IngredientWithSize itemIn, FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy)
		IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create("create_ehv_insulation", ItemStack.EMPTY, new FluidStack(getFluid(BlockCategoryFlags.FLUID), 100), IngredientWithSize.of(MetalEnum.Aluminum.getStack(ItemCategoryFlags.METAL_OXIDE)), new FluidTagInput(IETags.fluidResin, 100), null, null, 200, 51200);

	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return flag == ItemCategoryFlags.PLATE ? new ResourceLocation(IGLib.MODID, "item/greyscale/metal/plate") : super.getTextureLocation(flag);
	}
}
