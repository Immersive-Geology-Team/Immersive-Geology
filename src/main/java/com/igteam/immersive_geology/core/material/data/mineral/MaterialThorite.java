package com.igteam.immersive_geology.core.material.data.mineral;

import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;

import java.util.function.Function;

public class MaterialThorite extends MaterialMineral {

    public MaterialThorite() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xaa7547));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }
}
