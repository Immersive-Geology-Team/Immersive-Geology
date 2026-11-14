package com.igteam.immersivegeology.core.material.data.stone.compat.tfc;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.function.BiFunction;

public class MaterialAndesite extends MaterialTFCRawStone {

    public MaterialAndesite() {
        super();
        this.name="andesite";
        this.STONE_FORMATION = StoneFormation.IGNEOUS_EXTRUSIVE;
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }
}
