package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;

import java.util.function.Function;

public class MaterialSilver extends MaterialNativeMetal {

    public MaterialSilver() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ModFlags.TFC);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xe7e7f7));
    }
}
