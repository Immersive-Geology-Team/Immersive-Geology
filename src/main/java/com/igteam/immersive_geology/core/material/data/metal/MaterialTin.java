package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.helper.IFlagType;

import java.util.function.Function;

public class MaterialTin extends MaterialMetal {

    public MaterialTin() {
        super("tin");
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x919191));
    }
}
