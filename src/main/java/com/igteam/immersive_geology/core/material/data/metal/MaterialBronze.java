package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.flags.MaterialFlags;

import java.util.function.Function;

public class MaterialBronze extends MaterialMetal {

    public MaterialBronze() {
        super();
        addFlags(MaterialFlags.IS_METAL_ALLOY);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xd0d5db));
    }
}
