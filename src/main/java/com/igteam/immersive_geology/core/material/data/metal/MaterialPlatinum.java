package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersive_geology.core.material.helper.IFlagType;

import java.util.function.Function;

public class MaterialPlatinum extends MaterialNativeMetal {

    public MaterialPlatinum() {
        super("platinum");
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xE5E1E6));
    }
}
