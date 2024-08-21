package com.igteam.immersivegeology.core.material.data.stone.compat.tfc;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MaterialRhyolite extends MaterialTFCRawStone {

    public MaterialRhyolite() {
        super();
        this.STONE_FORMATION = StoneFormation.IGNEOUS_EXTRUSIVE;
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ModFlags.TFC);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }
}
