package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;

public class MaterialMetalAlloy extends MaterialMetal {

    public MaterialMetalAlloy(){
        super();
        addFlags(MaterialFlags.IS_METAL_ALLOY);
        removeMaterialFlags(ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST);
    }
}