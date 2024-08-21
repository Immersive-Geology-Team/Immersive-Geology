package com.igteam.immersivegeology.core.material.data.stone.compat.adastra;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.Function;

public class MaterialMoonStone extends MaterialAdAstraEarthStone
{

    public MaterialMoonStone() {
        super();
        this.name = "moon_stone"; // Special Case as we need to override the deafult name assignment method
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }
}
