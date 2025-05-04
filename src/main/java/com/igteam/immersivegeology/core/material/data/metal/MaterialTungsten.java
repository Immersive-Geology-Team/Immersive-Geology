/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class MaterialTungsten extends MaterialMetal {

    public MaterialTungsten() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET, BlockCategoryFlags.SCAFFOLDING, BlockCategoryFlags.FENCE, BlockCategoryFlags.ENGINEERING_BLOCK);
        removeMaterialFlags(ItemCategoryFlags.SLAG, ItemCategoryFlags.CRYSTAL);
        addFlags(ItemCategoryFlags.POWDER, ItemCategoryFlags.MECHANICAL_COMPONENT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff444D6A));
    }

    @Override
    public void setupRecipeStages()
    {
        IGMethodBuilder.crushing(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.INGOT, ItemCategoryFlags.GRIT, 6000, 100);

        IGMethodBuilder.pulverization(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 6000, 51200);

        IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.OXIDE_PELLET);
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.OXIDE_PELLET),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT), 1800);
        //Direct Leaching in HCL
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"slurry_to_compound_dust",
                getStack(ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT),
                ChemicalEnum.ChemicalWaste.getFluidStack(IGLib.ACID_RECOVERED_FROM_SLURRY),
                IngredientWithSize.of (ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Tungsten), IGLib.SLURRY_TO_CRYSTAL_MB),
                new FluidTagInput(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), IGLib.SLURRY_TO_CRYSTAL_MB),
                null,200, 51200);

        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST,
                1, 300, 153600);
    }
}
