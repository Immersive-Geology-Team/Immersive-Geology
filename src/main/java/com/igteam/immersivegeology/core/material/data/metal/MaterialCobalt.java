/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialCobalt extends MaterialMetal {

    public MaterialCobalt() {
        super();
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x1A79FF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                "slurry_"+getName() +"_to_" + getByproductMaterial().getName() + "_crystal",
                getStack(ItemCategoryFlags.CRYSTAL, IGLib.COMPOUND_FROM_ACID_AMOUNT),
                ChemicalEnum.NitricAcid.getFluidStack(IGLib.ACID_RECOVERED_FROM_SLURRY),
                ChemicalEnum.NitricAcid.getSlurryTagWith(MetalEnum.Cobalt), IGLib.SLURRY_TO_CRYSTAL_MB,
                300, 38400);

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.INGOT);

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.GRIT, ItemCategoryFlags.INGOT);
    }
}
