package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.stone.compat.adastra.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialAndesite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialDiorite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialGranite;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum StoneEnum implements MaterialInterface<MaterialStone> {
    //===== Terra Firma Craft =====\\
    Andesite(new MaterialAndesite()),
    Basalt(new MaterialBasalt()),
    Dacite(new MaterialDacite()),
    Rhyolite(new MaterialRhyolite()),

    Diorite(new MaterialDiorite()),
    Gabbro(new MaterialGabbro()),
    Granite(new MaterialGranite()),

    Gneiss(new MaterialGneiss()),
    Marble(new MaterialMarble()),
    Phyllite(new MaterialPhyllite()),
    Quartzite(new MaterialQuartzite()),
    Schist(new MaterialSchist()),
    Slate(new MaterialSlate()),

    Chalk(new MaterialChalk()),
    Chert(new MaterialChert()),
    Claystone(new MaterialClaystone()),
    Conglomerate(new MaterialConglomerate()),
    Dolomite(new MaterialDolomite()),
    Limestone(new MaterialLimestone()),
    Shale(new MaterialShale()),


    //===== Minecraft Stones =====\\
    MCStone(new MaterialVanilla()),
    MCDeepslate(new MaterialMCDeepslate()),
    MCAndesite(new MaterialMCAndesite()),
    MCDiorite(new MaterialMCDiorite()),
    MCGranite(new MaterialMCGranite()),

    //===== Minecraft Sands =====\\
    //Sand(new MaterialSand()),


    //===== Beyond Earth / Ad Astra =====\\
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
