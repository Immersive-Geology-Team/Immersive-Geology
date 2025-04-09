/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;

public class MaterialMagnesium extends MaterialMetal {

    public MaterialMagnesium() {
        super();
        removeMaterialFlags(ItemCategoryFlags.WIRE);
        removeMaterialFlags(ItemCategoryFlags.METAL_OXIDE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xaaa9ad));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.HydrochloricAcid,
                ItemCategoryFlags.CRYSTAL);
    }
}
