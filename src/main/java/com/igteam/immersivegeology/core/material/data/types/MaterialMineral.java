package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGArcSmeltingMethod;

import java.util.HashSet;
import java.util.Set;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        addFlags(MaterialFlags.HAS_SLURRY, ItemCategoryFlags.SLAG);

    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());
        new IGRecipeStage(this, IGStageDesignation.PREPARATION)
        {
            @Override
            protected void describe()
            {
                //IGMethodBuilder.crushing(this).create();
            }
        };
        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
