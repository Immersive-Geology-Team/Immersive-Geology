package com.igteam.immersive_geology.core.material;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.metal.*;
import com.igteam.immersive_geology.core.material.helper.MaterialInterface;

public enum MetalEnum implements MaterialInterface<MaterialMetal> {
    Chromium(new MaterialChromium()),
    Platinum(new MaterialPlatinum()),
    Tin(new MaterialTin());
    private final MaterialMetal material;
    MetalEnum(MaterialMetal m){
        this.material = m;
    }
    @Override
    public MaterialMetal instance() {
        return material;
    }
}
