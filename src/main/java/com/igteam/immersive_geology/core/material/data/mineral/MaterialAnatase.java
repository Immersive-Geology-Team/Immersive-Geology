package com.igteam.immersive_geology.core.material.data.mineral;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;

import java.util.function.Function;

public class MaterialAnatase extends MaterialMineral {

    public MaterialAnatase() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x475B74));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }
}
