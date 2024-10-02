/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialUnobtanium extends MaterialMetal {

    public MaterialUnobtanium() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x444D6A));
    }
}
