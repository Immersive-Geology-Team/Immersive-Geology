package com.igteam.immersive_geology.core.material.data.enums;

import com.igteam.immersive_geology.core.material.data.types.MaterialMetal;
import com.igteam.immersive_geology.core.material.data.metal.*;
import com.igteam.immersive_geology.core.material.helper.material.MaterialInterface;

public enum MetalEnum implements MaterialInterface<MaterialMetal> {
    Aluminum(new MaterialAluminium()),
    Bronze(new MaterialBronze()),
    Chromium(new MaterialChromium()),
    Cobalt(new MaterialCobalt()),
    Copper(new MaterialCopper()),
    Gold(new MaterialGold()),
    Iron(new MaterialIron()),
    Lead(new MaterialLead()),
    Manganese(new MaterialManganese()),
    Neodymium(new MaterialNeodymium()),
    Nickel(new MaterialNickel()),
    Osmium(new MaterialOsmium()),
    Platinum(new MaterialPlatinum()),
    Silver(new MaterialSilver()),
    Thorium(new MaterialThorium()),
    Tin(new MaterialTin()),
    Titanium(new MaterialTitanium()),
    Tungsten(new MaterialTungsten()),
    Unobtanium(new MaterialUnobtanium()),
    Uranium(new MaterialUranium()),
    Vanadium(new MaterialVanadium()),
    Zinc(new MaterialZinc()),
    Zirconium(new MaterialZirconium());

    private final MaterialMetal material;
    MetalEnum(MaterialMetal m){
        this.material = m;
    }
    @Override
    public MaterialMetal instance() {
        return material;
    }
}
