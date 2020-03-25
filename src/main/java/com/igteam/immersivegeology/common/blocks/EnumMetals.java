package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;

public enum EnumMetals {

    Copper(new MaterialMetalCopper());

    public final MaterialMetalBase metal;

    EnumMetals(MaterialMetalBase metal)
    {
        this.metal = metal;
    }
}
