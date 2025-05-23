/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.ToolTierHelper;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.world.item.Tier;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialUnobtanium extends MaterialMetal {

    public MaterialUnobtanium() {
        super();
        addFlags(ItemCategoryFlags.TOOL_HOE);
    }

    public Tier getToolTier()
    {
        return ToolTierHelper.UNOBTANIUM;
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff444D6A));
    }
}
