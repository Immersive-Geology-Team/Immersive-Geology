package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialManganese extends MaterialMetal {

    public MaterialManganese() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xaaa9ad));
    }
}
