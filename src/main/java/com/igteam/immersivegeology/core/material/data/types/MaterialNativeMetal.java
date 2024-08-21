package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

public class MaterialNativeMetal extends MaterialMetal {

    public MaterialNativeMetal(){
        super();
        addFlags(BlockCategoryFlags.ORE_BLOCK);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
    }
}