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
    SULFURIC_ACID(new MaterialSulfuricAcid()),
    BRINE(new MaterialBrine()),
    HYDROCHLORIC_ACID(new MaterialHydrochloricAcid()),
    HYDROFLUORIC_ACID(new MaterialHydrofluoricAcid()),
    NITRIC_ACID(new MaterialNitricAcid()),
    SODIUM_HYDROXIDE(new MaterialSodiumHydroxide()),

    // Mantle Fluids
    MOLTEN_MANTLE(new MaterialMoltenMantle()),
    IRON_MOLTEN_MANTLE(new MaterialIronRichMantle()),
    COPPER_MOLTEN_MANTLE(new MaterialCopperRichMantle()),
    GOLD_MOLTEN_MANTLE(new MaterialGoldRichMantle()),
    ALUMINUM_MOLTEN_MANTLE(new MaterialAluminumRichMantle());

    private final MaterialChemical material;
    ChemicalEnum(MaterialChemical m){
        this.material = m;
    }
    @Override
    public MaterialChemical instance() {
        return material;
    }
}
