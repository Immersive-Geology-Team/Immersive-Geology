package com.igteam.immersive_geology.core.material.data.enums;

import com.igteam.immersive_geology.core.material.data.stone.MaterialVanilla;
import com.igteam.immersive_geology.core.material.data.types.MaterialStone;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;

public enum StoneEnum implements MaterialInterface<MaterialStone> {
    Stone(new MaterialVanilla());

    private final MaterialStone material;
    StoneEnum(MaterialStone m){
        this.material = m;
    }
    @Override
    public MaterialStone instance() {
        return material;
    }
}
