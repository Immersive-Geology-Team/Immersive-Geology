package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;

public enum EnumMetals {

    Copper(new MaterialMetalCopper(), Type.IE_PURE)
    /*, TODO add all Materials
    Aluminum(new MaterialMetalAluminum(), Type.IE_PURE),
    Lead(new MaterialMetalLead(), Type.IE_PURE),
    Silver(new MaterialMetalSilver(), Type.IE_PURE),
    Nickel(new MaterialMetalNickel(), Type.IE_PURE),
    Uranium(new MaterialMetalUranium(), Type.IE_PURE),
    Constantan(new MaterialMetalConstantan(), Type.IE_ALLOY),
    Electrum(new MaterialMetalElectrum(), Type.IE_ALLOY),
    Steel(new MaterialMetalSteel(), Type.IE_ALLOY),
    Iron(new MaterialMetalIron(), Type.VANILLA),
    Gold(new MaterialMetalGold(), Type.VANILLA) */
    ;

    public final MaterialMetalBase metal;
    private final Type type;

    EnumMetals(MaterialMetalBase metal, Type t)
    {
        this.type = t;
        this.metal = metal;
    }

    EnumMetals(MaterialMetalBase metal)
    {
        this.metal = metal;
        this.type = Type.IG_PURE;
    }

    public String getName()
    {
        return this.toString().toLowerCase();
    }

    public boolean isVanillaMetal()
    {
        return type==Type.VANILLA;
    }

    public boolean isIGMetal()
    {
        return type==Type.IG_PURE || type==Type.IG_ALLOY;
    }

    public boolean isPureIGMetal()
    {
        return type==Type.IG_PURE;
    }

    public boolean isAlloyIGMetal()
    {
        return type==Type.IG_ALLOY;
    }

    private enum Type
    {
        VANILLA,
        IE_PURE,
        IE_ALLOY,
        IG_PURE,
        IG_ALLOY
    }


}
