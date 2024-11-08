/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import blusunrize.immersiveengineering.common.config.IEServerConfig.Ores.VeinType;
import com.igteam.immersivegeology.core.material.data.mineral.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.serialization.Codec;

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
    Molybenite(new MaterialMolybenite()),
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

    public static final Codec<MineralEnum> CODEC = Codec.STRING.xmap(MineralEnum::valueOf, Enum::name);

    private final MaterialMineral material;
    MineralEnum(MaterialMineral m){
        this.material = m;
    }
    @Override
    public MaterialMineral instance() {
        return material;
    }

	public int getVeinSize()
	{
        return 24;
	}

    public int getMinY()
    {
        return 32;
    }

    public int getMaxY()
    {
        return 112;
    }

    public int veinsPerChunk()
    {
        return 1;
    }

    public double airExposure()
    {
        return 0.5;
    }

    public int rarity()
    {
        return 50;
    }
}
