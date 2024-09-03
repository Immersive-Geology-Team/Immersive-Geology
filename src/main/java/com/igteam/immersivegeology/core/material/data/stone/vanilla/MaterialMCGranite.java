package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.function.Function;

public class MaterialMCGranite extends MaterialStone {

    public MaterialMCGranite() {
        super();
        this.name = "granite"; // Special Case as we need to override the deafult name assignment method
        this.STONE_FORMATION = StoneFormation.IGNEOUS_INTRUSIVE;
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }
}
