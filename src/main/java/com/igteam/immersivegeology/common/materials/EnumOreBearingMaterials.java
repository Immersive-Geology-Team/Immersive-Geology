package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.common.materials.crystals.MaterialCrystalQuartz;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralCassiterite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralChalcopyrite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralChromite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralCryolite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralFerberite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralFluorite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralGypsum;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralHematite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralHubnerite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralMagnetite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralPyrolusite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralRockSalt;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralThorianite;
import com.igteam.immersivegeology.common.materials.minerals.MaterialMineralUraninite;

import net.minecraft.util.IStringSerializable;

public enum EnumOreBearingMaterials {
	Quartz("quartz", MaterialCrystalQuartz.getStaticColor()),

	// Minerals
	Casserite("casserite", MaterialMineralCassiterite.getStaticColor()),
	Chalcopyrite("chalcopyrite", MaterialMineralChalcopyrite.getStaticColor()),
	Chromite("chromite", MaterialMineralChromite.getStaticColor()), 
	Cryolite("cryolite", MaterialMineralCryolite.getStaticColor()),
	Ferberite("ferberite", MaterialMineralFerberite.getStaticColor()),
	Fluorite("fluorite", MaterialMineralFluorite.getStaticColor()), 
	Gypsum("gypsum", MaterialMineralGypsum.getStaticColor()),
	Hematite("hematite", MaterialMineralHematite.getStaticColor()),
	Hubnerite("hubnerite", MaterialMineralHubnerite.getStaticColor()),
	Magnetite("magnetite", MaterialMineralMagnetite.getStaticColor()),
	Pyrolusite("pyrolusite", MaterialMineralPyrolusite.getStaticColor()),
	RockSalt("rock_salt", MaterialMineralRockSalt.getStaticColor()), 
	Thorite("thorite", MaterialMineralThorianite.getStaticColor()),
	Uraninite("uraninite", MaterialMineralUraninite.getStaticColor());

	int oreColor;
	String name;
	
	EnumOreBearingMaterials(String oreName, int colorCode)
	{
		this.oreColor = colorCode;
		this.name = oreName;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return name.toLowerCase();
	}

	public int getColor() {
		// TODO Auto-generated method stub
		return oreColor;
	}

}
