package com.igteam.immersive_geology.core.material;

import com.igteam.immersive_geology.core.material.data.metal.MaterialChromium;
import com.igteam.immersive_geology.core.material.data.metal.MaterialPlatinum;
import com.igteam.immersive_geology.core.material.data.metal.MaterialTin;
import com.igteam.immersive_geology.core.material.data.stone.MaterialVanilla;
import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.types.MaterialStone;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;

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
