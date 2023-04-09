package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialChromium extends MaterialMetal {

    public MaterialChromium() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xD7B4F3));
    }
}
