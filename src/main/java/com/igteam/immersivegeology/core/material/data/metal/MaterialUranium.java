/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialRadioactiveMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;

public class MaterialUranium extends MaterialRadioactiveMetal
{
    public MaterialUranium() {
        super();
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB, BlockCategoryFlags.SHEETMETAL_SLAB);
        addFlags(ItemCategoryFlags.COMPOUND_DUST);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff759068));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }


    @Override
    public void setupRecipeStages()
    {
        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST,
                1, 300).setMVHeat();

        //Fuck that, we sinter U oxides to ingot
        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.INGOT,
                ItemCategoryFlags.METAL_OXIDE,
                1, 300).setHVHeat();
    }
}
