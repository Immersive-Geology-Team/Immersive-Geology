package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;

public enum EnumMetals {

    Copper(new MaterialMetalCopper(), Type.IE_PURE);

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
