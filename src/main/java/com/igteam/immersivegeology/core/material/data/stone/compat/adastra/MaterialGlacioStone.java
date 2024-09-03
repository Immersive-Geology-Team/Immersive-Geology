package com.igteam.immersivegeology.core.material.data.stone.compat.adastra;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.function.Function;

public class MaterialGlacioStone extends MaterialAdAstraStone
{

    public MaterialGlacioStone() {
        super();
        this.name = "glacio_stone"; // Special Case as we need to override the deafult name assignment method
        this.STONE_FORMATION = StoneFormation.SEDIMENTARY;
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }
}
