package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class MaterialStone extends GeologyMaterial {

    protected StoneFormation STONE_FORMATION = StoneFormation.IGNEOUS_INTRUSIVE;

    public MaterialStone() {
        super();
        addFlags(MaterialFlags.IS_ORE_BEARING);
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        return new ResourceLocation("minecraft", "block/"+getName());
    }

    public StoneFormation getStoneFormation()
    {
        return this.STONE_FORMATION;
    }

    @Override
    public void setupRecipeStages()
    {

    }
}
