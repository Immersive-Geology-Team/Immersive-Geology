package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.stone.compat.beyond_earth.*;
import com.igteam.immersivegeology.core.material.data.stone.sand.MaterialSand;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum StoneEnum implements MaterialInterface<MaterialStone> {

    //===== Minecraft Stones =====\\
    Stone(new MaterialVanilla()),
    Deepslate(new MaterialDeepslate()),
    Andesite(new MaterialAndesite()),
    Diorite(new MaterialDiorite()),
    Granite(new MaterialGranite()),

    //===== Minecraft Sands =====\\
    Sand(new MaterialSand()),


    //===== Beyond Earth =====\\
    MoonStone(new MaterialMoonStone()),
    MarsStone(new MaterialMarsStone()),
    MercuryStone(new MaterialMercuryStone()),
    VenusStone(new MaterialVenusStone()),
    GlacioStone(new MaterialGlacioStone());

    private final MaterialStone material;
    StoneEnum(MaterialStone m){
        this.material = m;
    }
    @Override
    public MaterialStone instance() {
        return material;
    }
}
