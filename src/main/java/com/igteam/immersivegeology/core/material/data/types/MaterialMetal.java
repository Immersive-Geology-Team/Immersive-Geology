/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;

public class MaterialMetal extends GeologyMaterial {

    public MaterialMetal(){
        super();
        addFlags(BlockCategoryFlags.SLAB, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.DUST, MaterialFlags.IS_MOLTEN_METAL, MaterialFlags.HAS_SLURRY);
    }

    @Override
    public void setupRecipeStages()
    {

    }
}
