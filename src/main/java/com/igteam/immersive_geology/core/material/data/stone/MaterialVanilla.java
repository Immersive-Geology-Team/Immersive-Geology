package com.igteam.immersive_geology.core.material.data.stone;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.types.MaterialStone;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.MaterialFlags;

import java.util.function.Function;

public class MaterialVanilla extends MaterialStone {

    public MaterialVanilla() {
        super("stone");
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x888c8d));
    }
}
