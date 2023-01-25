package com.igteam.immersive_geology.material;

import com.igteam.immersive_geology.material.helper.CategoryFlags;

public class MaterialMetal extends GeologyMaterial {

    public MaterialMetal(){
        addCategories(CategoryFlags.INGOT, CategoryFlags.PLATE);
    }

}
