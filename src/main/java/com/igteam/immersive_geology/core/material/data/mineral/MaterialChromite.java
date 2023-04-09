package com.igteam.immersive_geology.core.material.data.mineral;
            
import com.igteam.immersive_geology.core.material.data.types.MaterialMineral;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialChromite extends MaterialMineral {

    public MaterialChromite() {
        super();
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x615964));
    }
}
