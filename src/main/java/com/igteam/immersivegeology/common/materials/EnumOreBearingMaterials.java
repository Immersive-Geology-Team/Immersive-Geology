package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.common.materials.crystals.*;
import com.igteam.immersivegeology.common.materials.minerals.*;

public enum EnumOreBearingMaterials {
	Quartz(MaterialCrystalQuartz.getStaticColor()),

	// Minerals
	Casserite(MaterialMineralCassiterite.getStaticColor()),
	Thorite(MaterialMineralThorianite.getStaticColor()),
	Uraninite(MaterialMineralUraninite.getStaticColor());
	
	/*
	 * 	
	Chalcopyrite(MaterialMineralChalcopyrite.getStaticColor()),
	Chromite(MaterialMineralChromite.getStaticColor()), 
	Cryolite(MaterialMineralCryolite.getStaticColor());
	
	Ferberite(MaterialMineralFerberite.getStaticColor()),
	Fluorite(MaterialMineralFluorite.getStaticColor()), 
	Gypsum(MaterialMineralGypsum.getStaticColor()),
	Hematite(MaterialMineralHematite.getStaticColor()),
	Hubnerite(MaterialMineralHubnerite.getStaticColor()),
	Magnetite(MaterialMineralMagnetite.getStaticColor()),
	Pyrolusite(MaterialMineralPyrolusite.getStaticColor()),
	RockSalt(MaterialMineralRockSalt.getStaticColor()), 
	
	 * 
	 */

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
