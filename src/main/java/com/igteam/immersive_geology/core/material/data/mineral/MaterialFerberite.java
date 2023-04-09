package com.igteam.immersive_geology.core.material.data.mineral;

import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;

import java.util.function.Function;

public class MaterialFerberite extends MaterialMineral {

    public MaterialFerberite() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x2688D1));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }
}
