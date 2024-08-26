package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;

public class MaterialMetal extends GeologyMaterial {

    public MaterialMetal(){
        super();
        addFlags(BlockCategoryFlags.SLAB);
    }
}
