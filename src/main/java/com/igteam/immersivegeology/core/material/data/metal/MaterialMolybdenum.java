/*
M
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
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

public class MaterialMolybdenum extends MaterialMetal {

    public MaterialMolybdenum() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
        removeMaterialFlags(ItemCategoryFlags.WIRE, ItemCategoryFlags.GEAR);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffa1a8b2));
    }

    @Override
    public void setupRecipeStages()
    {
        IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.OXIDE_PELLET);
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.OXIDE_PELLET),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT), 1800);

        IGMethodBuilder.centrifuge(this, IGStageDesignation.PURIFICATION).create(
                ChemicalEnum.Ammonia.getCloudySlurryTagWith(MineralEnum.Molybdenite),
                IGLib.SLURRY_TO_CRYSTAL_MB, MetalEnum.Molybdenum, ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT,
                ChemicalEnum.ChemicalWaste.getCloudySlurryWith(MineralEnum.Molybdenite),
                IGLib.ACID_RECOVERED_FROM_SLURRY, null, 0, 1200, 614400);

        IGMethodBuilder.decompose(this, IGStageDesignation.ROASTING).create(
                ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST,
                1, 300, RotaryKilnLogic.HV_HEAT_CAP);
    }
}
