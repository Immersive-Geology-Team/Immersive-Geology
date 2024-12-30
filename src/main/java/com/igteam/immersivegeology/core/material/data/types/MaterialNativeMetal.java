package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.Optional;

public class MaterialNativeMetal extends MaterialMetal {

    public MaterialNativeMetal(){
        super();
        addFlags(BlockCategoryFlags.ORE_BLOCK);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(8,50,1,-48,112,10,0.5, false, Optional.empty());
    }
}