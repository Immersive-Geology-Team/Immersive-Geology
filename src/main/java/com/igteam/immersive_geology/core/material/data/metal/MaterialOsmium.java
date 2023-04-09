package com.igteam.immersive_geology.core.material.data.metal;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;

import java.util.function.Function;

public class MaterialOsmium extends MaterialMetal {

    public MaterialOsmium() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x8A9A9A));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
}
