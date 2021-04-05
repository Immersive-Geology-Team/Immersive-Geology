package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaExtrusive;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaIntrusive;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaMetamorphic;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaSedimentary;

public enum StoneEnum {
    Igneous_Intrusive(new MaterialStoneVanillaIntrusive()),
    Igneous_Extrusive(new MaterialStoneVanillaExtrusive()),
    Metamorphic(new MaterialStoneVanillaMetamorphic()),
    Sedimentary(new MaterialStoneVanillaSedimentary()); //This is all Vanilla Stone stuff, added more stone types will make a new Stone Block, Ore Block, Cobblestone Block... etc.

    private Material material;
    StoneEnum(Material material){
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
