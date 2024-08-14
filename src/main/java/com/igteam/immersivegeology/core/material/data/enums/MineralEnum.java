package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.mineral.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

public enum MineralEnum implements MaterialInterface<MaterialMineral> {
    Acanthite(new MaterialAcanthite()),
    Alumina(new MaterialAlumina()),
    Anatase(new MaterialAnatase()),
    Bauxite(new MaterialBauxite()),
    Cassiterite(new MaterialCassiterite()),
    Chalcocite(new MaterialChalcocite()),
    Chalcopyrite(new MaterialChalcopyrite()),
    Chromite(new MaterialChromite()),
    Cobaltite(new MaterialCobaltite()),
    Cryolite(new MaterialCryolite()),
    Cuprite(new MaterialCuprite()),
    Ferberite(new MaterialFerberite()),
    Fluorite(new MaterialFluorite()),
    Galena(new MaterialGalena()),
    Gypsum(new MaterialGypsum()),
    Hematite(new MaterialHematite()),
    Hubnerite(new MaterialHubnerite()),
    Ilmenite(new MaterialIlmenite()),
    Kaolinite(new MaterialKaolinite()),
    Magnetite(new MaterialMagnetite()),
    Monazite(new MaterialMonazite()),
    Pyrite(new MaterialPyrite()),
    Pyrolusite(new MaterialPyrolusite()),
    Scheelite(new MaterialScheelite()),
    Smithsonite(new MaterialSmithsonite()),
    Sphalerite(new MaterialSphalerite()),
    Thorianite(new MaterialThorianite()),
    Thorite(new MaterialThorite()),
    Ullmannite(new MaterialUllmannite()),
    Unobtania(new MaterialUnobtania()),
    Uraninite(new MaterialUraninite()),
    Vanadinite(new MaterialVanadinite()),
    Wolframite(new MaterialWolframite()),
    Zircon(new MaterialZircon());

    private final MaterialMineral material;
    MineralEnum(MaterialMineral m){
        this.material = m;
    }
    @Override
    public MaterialMineral instance() {
        return material;
    }
}
