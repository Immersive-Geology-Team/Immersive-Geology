package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.resources.ResourceLocation;

public class MaterialMisc extends GeologyMaterial {

    public MaterialMisc() {
        super();
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        return new ResourceLocation("minecraft", "block/"+getName());
    }

    @Override
    public void setupRecipeStages()
    {

    }
}
