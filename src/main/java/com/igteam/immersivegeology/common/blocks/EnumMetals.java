package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.materials.metals.*;

public enum EnumMetals {

    Aluminum(new MaterialMetalAluminum(), Type.IE_PURE),
    Copper(new MaterialMetalCopper(), Type.IE_PURE),
    Gold(new MaterialMetalGold(), Type.VANILLA),
    Iron(new MaterialMetalIron(), Type.VANILLA),
    Lead(new MaterialMetalLead(), Type.IE_PURE),
    Nickel(new MaterialMetalNickel(), Type.IE_PURE),
    Silver(new MaterialMetalSilver(), Type.IE_PURE),
    Uranium(new MaterialMetalUranium(), Type.IE_PURE)
    /*, TODO add all Materials
    Constantan(new MaterialMetalConstantan(), Type.IE_ALLOY),
    Electrum(new MaterialMetalElectrum(), Type.IE_ALLOY),
    Steel(new MaterialMetalSteel(), Type.IE_ALLOY) */
    ;

    public final MaterialMetalBase metal;

    private final Type type;

    EnumMetals(MaterialMetalBase metal, Type t)
    {
        this.metal = metal;
        this.type = t;
    }

    private enum Type {
        VANILLA,
        IE_PURE,
        IE_ALLOY,
        IG_PURE,
        IG_ALLOY
    }
}
