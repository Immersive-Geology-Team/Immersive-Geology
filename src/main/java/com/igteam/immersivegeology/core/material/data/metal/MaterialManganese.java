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
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialManganese extends MaterialMetal {

    public MaterialManganese() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
        addFlags(ItemCategoryFlags.COMPOUND_DUST);
        removeMaterialFlags(ItemCategoryFlags.CRYSTAL);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xaaa9ad));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(
                getItemTag(ItemCategoryFlags.COMPOUND_DUST), getStack(ItemCategoryFlags.METAL_OXIDE, 1),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE, 1),
                0.8f, 200, 1000);
    }
}
