package com.igteam.immersive_geology.api.materials.fluid;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.MaterialFluidBrine;
import com.igteam.immersive_geology.api.materials.material_data.fluids.MaterialFluidWater;
import com.igteam.immersive_geology.api.materials.material_data.fluids.chemical.*;

import java.util.ArrayList;
import java.util.Arrays;

public enum FluidEnum {
    //Fluids
    //Non Volatile
    Brine(new MaterialFluidBrine()),

    //Volatile
    SulfuricAcid(new MaterialFluidSulfuricAcid()),
    HydrochloricAcid(new MaterialFluidHydrochloricAcid()),
    HydrofluoricAcid(new MaterialFluidHydrofluoricAcid()),
    NitricAcid(new MaterialFluidNitricAcid()),
    SodiumHydroxide(new MaterialFluidSodiumHydroxide());

    private final MaterialFluidBase material;

    FluidEnum(MaterialFluidBase material)
    {
        this.material = material;
    }

    public MaterialFluidBase getMaterial() {
        return material;
    }
}
