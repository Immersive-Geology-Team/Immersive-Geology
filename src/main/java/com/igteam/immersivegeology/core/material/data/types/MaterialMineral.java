package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGArcSmeltingMethod;

import java.util.HashSet;
import java.util.Set;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        addFlags(MaterialFlags.HAS_SLURRY);

    }

    @Override
    public void setupRecipeStages()
    {

    }
}
