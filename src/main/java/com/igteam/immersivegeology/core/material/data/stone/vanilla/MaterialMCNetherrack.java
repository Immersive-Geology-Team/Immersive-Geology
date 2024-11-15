package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.function.BiFunction;

public class MaterialMCNetherrack extends MaterialStone {

    public MaterialMCNetherrack() {
        super();
        this.name = "netherrack"; // Special Case as we need to override the default name assignment method
        this.STONE_FORMATION = StoneFormation.METAMORPHIC;
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0xff88c8d));
    }
}
