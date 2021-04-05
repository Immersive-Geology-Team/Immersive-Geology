package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.api.materials.material_data.MaterialEmpty;
import com.igteam.immersive_geology.api.materials.material_data.crystals.MaterialCrystalDiamond;
import com.igteam.immersive_geology.api.materials.material_data.crystals.MaterialCrystalPhlebotinum;
import com.igteam.immersive_geology.api.materials.material_data.crystals.MaterialCrystalQuartz;
import com.igteam.immersive_geology.api.materials.material_data.fluids.MaterialFluidBrine;
import com.igteam.immersive_geology.api.materials.material_data.fluids.MaterialFluidWater;
import com.igteam.immersive_geology.api.materials.material_data.metals.*;
import com.igteam.immersive_geology.api.materials.material_data.metals.alloys.MaterialMetalConstantan;
import com.igteam.immersive_geology.api.materials.material_data.metals.alloys.MaterialMetalElectrum;
import com.igteam.immersive_geology.api.materials.material_data.metals.alloys.MaterialMetalSteel;
import com.igteam.immersive_geology.api.materials.material_data.minerals.*;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaExtrusive;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaIntrusive;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaMetamorphic;
import com.igteam.immersive_geology.api.materials.material_data.stones.MaterialStoneVanillaSedimentary;

public enum MaterialEnum {
    //Empty
    Empty(new MaterialEmpty()),

    //Metals
    Aluminium(new MaterialMetalAluminium()),
    Chromium(new MaterialMetalChromium()),
    Copper(new MaterialMetalCopper()),
    Gold(new MaterialMetalGold()),
    Iron(new MaterialMetalIron()),
    Lead(new MaterialMetalLead()),
    Manganese(new MaterialMetalManganese()),
    Nickel(new MaterialMetalNickel()),
    Platinum(new MaterialMetalPlatinum()),
    Silver(new MaterialMetalSilver()),
    Cobalt(new MaterialMetalCobalt()),
    Uranium(new MaterialMetalUranium()),
    Tin(new MaterialMetalTin()),
    Titanium(new MaterialMetalTitanium()),
    Tungsten(new MaterialMetalTungsten()),
    Vanadium(new MaterialMetalVanadium()),
    Zirconium(new MaterialMetalZirconium()),
    Zinc(new MaterialMetalZinc()),

    Constantan(new MaterialMetalConstantan()),
    Electrum(new MaterialMetalElectrum()),
    Steel(new MaterialMetalSteel()),

    //Crystals
    Diamond(new MaterialCrystalDiamond()),
    Phlebotinum(new MaterialCrystalPhlebotinum()),
    Quartz(new MaterialCrystalQuartz()),

    //Minerals
    Anatase(new MaterialMineralAnatase()),
    Cuprite(new MaterialMineralCuprite()),
    Zircon(new MaterialMineralZircon()),
    Ilmenite(new MaterialMineralIlmenite()),
    Cobaltite(new MaterialMineralCobaltite()),
    Casserite(new MaterialMineralCassiterite()),
    Chalcopyrite(new MaterialMineralChalcopyrite()),
    Chromite(new MaterialMineralChromite()),
    Cryolite(new MaterialMineralCryolite()),
    Ferberite(new MaterialMineralFerberite()),
    Fluorite(new MaterialMineralFluorite()),
    Gypsum(new MaterialMineralGypsum()),
    Hematite(new MaterialMineralHematite()),
    Hubnerite(new MaterialMineralHubnerite()),
    Magnetite(new MaterialMineralMagnetite()),
    Pyrolusite(new MaterialMineralPyrolusite()),
    RockSalt(new MaterialMineralRockSalt()),
    Thorite(new MaterialMineralThorianite()),
    Uraninite(new MaterialMineralUraninite()),
    Sphalerite(new MaterialMineralSphalerite()),
    Ullmannite(new MaterialMineralUllmannite()),
    Galena(new MaterialMineralGalena()),
    Pyrite(new MaterialMineralPyrite()),
    Wolframite(new MaterialMineralWolframite()),
    Vanadinite(new MaterialMineralVanadinite()),

    //Fluids
    Water(new MaterialFluidWater()),
    Brine(new MaterialFluidBrine());

    private final Material material;

    MaterialEnum(Material material)
    {
        this.material = material;
    }

    public Material getMaterial(){
        return material;
    }
}
