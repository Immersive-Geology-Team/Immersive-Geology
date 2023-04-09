package com.igteam.immersive_geology.core.material.data.mineral;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;

import java.util.function.Function;

public class MaterialAcanthite extends MaterialMineral {

    public MaterialAcanthite() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x83C4EA));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }
}
