package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialPlatinum extends MaterialNativeMetal {

    public MaterialPlatinum() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xe7e7f7));
    }
}
