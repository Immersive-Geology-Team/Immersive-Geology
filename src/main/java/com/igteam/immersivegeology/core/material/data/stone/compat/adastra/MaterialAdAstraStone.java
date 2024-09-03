package com.igteam.immersivegeology.core.material.data.stone.compat.adastra;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MaterialAdAstraStone extends MaterialStone {

    public MaterialAdAstraStone() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ModFlags.AD_ASTRA);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction()
    {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag)
    {
        return new ResourceLocation("ad_astra", "block/"+getName());
    }
}
