package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.chemical.*;
import com.igteam.immersivegeology.core.material.data.chemical.mantle.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.adastra.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.*;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum ChemicalEnum implements MaterialInterface<MaterialChemical>
{
    SulfuricAcid(new MaterialSulfuricAcid()),
    SulfurDioxde(new MaterialSulfurDioxide()),
    Brine(new MaterialBrine()),
    HydrochloricAcid(new MaterialHydrochloricAcid()),
    HydrofluoricAcid(new MaterialHydrofluoricAcid()),
    NitricAcid(new MaterialNitricAcid()),
    SodiumHydroxide(new MaterialSodiumHydroxide()),

    // Mantle Fluid
    MoltenMantle(new MaterialMoltenMantle());

    private final MaterialChemical material;
    ChemicalEnum(MaterialChemical m){
        this.material = m;
    }
    @Override
    public MaterialChemical instance() {
        return material;
    }
}
