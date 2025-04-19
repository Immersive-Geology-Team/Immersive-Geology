/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.*;

import java.util.function.BiFunction;

public class MaterialSteel extends MaterialMetalAlloy
{

    public MaterialSteel() {
        super();
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB);
        addFlags(BlockCategoryFlags.CRATE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff85888C));
    }
}
