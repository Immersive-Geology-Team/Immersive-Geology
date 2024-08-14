package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialMagnetite extends MaterialMineral {

    public MaterialMagnetite() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xb4b7b0));
    }
}
