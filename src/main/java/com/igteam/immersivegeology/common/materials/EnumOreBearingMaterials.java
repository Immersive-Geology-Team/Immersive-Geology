package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.common.materials.crystals.*;
import com.igteam.immersivegeology.common.materials.minerals.*;

public enum EnumOreBearingMaterials {
	Quartz(MaterialCrystalQuartz.baseColor),

	// Minerals
	Casserite(MaterialMineralCassiterite.baseColor),
	Thorite(MaterialMineralThorianite.baseColor), 
	Chalcopyrite(MaterialMineralChalcopyrite.baseColor),
	Chromite(MaterialMineralChromite.baseColor), 
	Cryolite(MaterialMineralCryolite.baseColor),
	Ferberite(MaterialMineralFerberite.baseColor),
	Fluorite(MaterialMineralFluorite.baseColor), 
	Gypsum(MaterialMineralGypsum.baseColor),
	Hematite(MaterialMineralHematite.baseColor),
	Hubnerite(MaterialMineralHubnerite.baseColor),
	Magnetite(MaterialMineralMagnetite.baseColor),
	Pyrolusite(MaterialMineralPyrolusite.baseColor),
	RockSalt(MaterialMineralRockSalt.baseColor), 
	Uraninite(MaterialMineralUraninite.baseColor);

	int oreColor;
	EnumOreBearingMaterials(int colorCode)
	{
		this.oreColor = colorCode;
	}

	public int getColor() {
		// TODO Auto-generated method stub
		return oreColor;
	}
}
