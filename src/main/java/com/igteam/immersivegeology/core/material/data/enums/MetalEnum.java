/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.chemical.mantle.MaterialMoltenMantle;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.metal.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum MetalEnum implements MaterialInterface<MaterialMetal> {
    Aluminum(new MaterialAluminum()),
    Bronze(new MaterialBronze()),
    Chromium(new MaterialChromium()),
    Cobalt(new MaterialCobalt()),
    Copper(new MaterialCopper()),
    Gold(new MaterialGold()),
    Iron(new MaterialIron()),
    Lead(new MaterialLead()),
    Manganese(new MaterialManganese()),
    Molybdenum(new MaterialMolybdenum()),
    Neodymium(new MaterialNeodymium()),
    Nickel(new MaterialNickel()),
    Osmium(new MaterialOsmium()),
    Platinum(new MaterialPlatinum()),
    Hastelloy(new MaterialHastelloy()),
    Silver(new MaterialSilver()),
    Steel(new MaterialSteel()),
    Sodium(new MaterialSodium()),
    Thorium(new MaterialThorium()),
    Tin(new MaterialTin()),
    Titanium(new MaterialTitanium()),
    Tungsten(new MaterialTungsten()),
    Unobtanium(new MaterialUnobtanium()),
    Uranium(new MaterialUranium()),
    Vanadium(new MaterialVanadium()),
    Zinc(new MaterialZinc()),
    Zirconium(new MaterialZirconium()),
    // Mantle Fluid
    MoltenMantle(new MaterialMoltenMantle());

    private final MaterialMetal material;
    MetalEnum(MaterialMetal m){
        this.material = m;
    }
    @Override
    public MaterialMetal instance() {
        return material;
    }
}
