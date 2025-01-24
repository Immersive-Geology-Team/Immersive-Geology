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
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChromium extends MaterialMetal {

    public MaterialChromium() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xD7B4F3));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.INGOT);

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.GRIT, ItemCategoryFlags.INGOT);
    }
}
