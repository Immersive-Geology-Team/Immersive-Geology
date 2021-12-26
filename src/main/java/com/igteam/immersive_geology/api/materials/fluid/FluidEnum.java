package com.igteam.immersive_geology.api.materials.fluid;

import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.MaterialFluidBrine;
import com.igteam.immersive_geology.api.materials.material_data.fluids.chemical.*;
import com.igteam.immersive_geology.api.materials.material_data.gases.MaterialGasHydrogen;
import com.igteam.immersive_geology.api.materials.material_data.gases.MaterialGasSulfuric;

public enum FluidEnum {
    //Fluids
    //Non Volatile
    Brine(new MaterialFluidBrine()),

    //Volatile
    SulfuricAcid(new MaterialFluidSulfuricAcid()),
    HydrochloricAcid(new MaterialFluidHydrochloricAcid()),
    HydrofluoricAcid(new MaterialFluidHydrofluoricAcid()),
    NitricAcid(new MaterialFluidNitricAcid()),
    SodiumHydroxide(new MaterialFluidSodiumHydroxide()),

    //Gas
    SulfurDioxide(new MaterialGasSulfuric()),
    Hydrogen(new MaterialGasHydrogen());

    private final MaterialFluidBase material;

    FluidEnum(MaterialFluidBase material)
    {
        this.material = material;
    }

    public MaterialFluidBase getMaterial() {
        return material;
    }
}
