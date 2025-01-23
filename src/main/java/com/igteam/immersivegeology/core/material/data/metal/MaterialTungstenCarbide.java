/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import java.util.function.BiFunction;

public class MaterialTungstenCarbide extends MaterialMetalAlloy
{

    public MaterialTungstenCarbide() {
        super();
        removeMaterialFlags(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST, BlockCategoryFlags.SLURRY);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x264e4c));
    }
}
