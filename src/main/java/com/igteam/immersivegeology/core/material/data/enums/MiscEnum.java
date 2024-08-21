package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.metal.*;
import com.igteam.immersivegeology.core.material.data.stone.special.MaterialRefractoryBlock;
import com.igteam.immersivegeology.core.material.data.stone.special.MaterialReinforcedRefractoryBlock;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum MiscEnum implements MaterialInterface<MaterialMisc> {
    //===== IG Special Materials =====\\
    Refractory(new MaterialRefractoryBlock()),
    ReinforcedRefractory(new MaterialReinforcedRefractoryBlock());

    private final MaterialMisc material;
    MiscEnum(MaterialMisc m){
        this.material = m;
    }
    @Override
    public MaterialMisc instance() {
        return material;
    }
}
