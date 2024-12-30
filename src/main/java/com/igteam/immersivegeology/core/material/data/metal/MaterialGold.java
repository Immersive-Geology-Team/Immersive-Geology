/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.common.world.FeatureMineralVein;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialGold extends MaterialNativeMetal {

    public MaterialGold() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE, ItemCategoryFlags.DUST);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);

        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.CONFIG = new MaterialMineral.MineralConfig(8,25,1,0,120,125,0.5, false, Optional.empty());
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xFFD700));
    }
}
