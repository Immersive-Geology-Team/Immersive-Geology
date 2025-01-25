/*
M
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
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialMolybdenum extends MaterialMetal {

    public MaterialMolybdenum() {
        super();
        removeMaterialFlags(ItemCategoryFlags.WIRE, ItemCategoryFlags.GEAR);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xa1a8b2));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.centrifuge(this, IGStageDesignation.PURIFICATION).create(
                ChemicalEnum.Ammonia.getCloudySlurryTagWith(MineralEnum.Molybenite),
                IGLib.SLURRY_TO_CRYSTAL_MB, MetalEnum.Molybdenum, ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT,
                ChemicalEnum.ChemicalWaste.getCloudySlurryWith(MineralEnum.Molybenite),
                IGLib.ACID_RECOVERED_FROM_SLURRY, null, 0, 1200, 614400);

        IGMethodBuilder.decompose(this, IGStageDesignation.ROASTING).create(
            ItemCategoryFlags.COMPOUND_DUST,
                ItemCategoryFlags.METAL_OXIDE,
                1, 300, 153600);
    }
}
