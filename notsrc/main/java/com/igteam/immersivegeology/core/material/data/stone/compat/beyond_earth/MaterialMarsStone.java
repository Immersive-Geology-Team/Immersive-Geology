package com.igteam.immersivegeology.core.material.data.stone.compat.beyond_earth;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MaterialMarsStone extends MaterialStone {

    public MaterialMarsStone() {
        super();
        this.name = "mars_stone"; // Special Case as we need to override the deafult name assignment method
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ModFlags.BEYOND_EARTH);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        return new ResourceLocation("beyond_earth", "blocks/"+getName());
    }
}
