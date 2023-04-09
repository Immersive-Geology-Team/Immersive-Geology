package com.igteam.immersive_geology.core.material.data.types;

import com.igteam.immersive_geology.core.material.GeologyMaterial;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import net.minecraft.resources.ResourceLocation;

public class MaterialStone extends GeologyMaterial {

    public MaterialStone() {
        super();
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        return new ResourceLocation("minecraft", "block/"+getName());
    }
}
