/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;

public class MaterialAluminum extends MaterialMetal {

    public MaterialAluminum()
    {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT,
                ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE,
                ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK,
                BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SHEETMETAL_STAIRS,
                BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB, BlockCategoryFlags.SHEETMETAL_SLAB);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xd0d5db));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST,
                1, 300, 153600);
    }
}
