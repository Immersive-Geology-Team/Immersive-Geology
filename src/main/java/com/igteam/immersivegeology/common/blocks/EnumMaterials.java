package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.common.materials.MaterialEmpty;
import com.igteam.immersivegeology.common.materials.metals.*;
import com.igteam.immersivegeology.common.materials.minerals.*;

import java.util.stream.Stream;

public enum EnumMaterials
{

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
	Tin(new MaterialMetalTin()),
	Titanium(new MaterialMetalTitanium()),
	Tungsten(new MaterialMetalTungsten()),
	Uranium(new MaterialMetalUranium()),
	Vanadium(new MaterialMetalVanadium()),
	Zirconium(new MaterialMetalZirconium()),
	//Minerals
	Casserite(new MaterialMineralCassiterite()),
	Chalcopyrite(new MaterialMineralChalcopyrite()),
	Chromite(new MaterialMineralChromite()),
	Cryolite(new MaterialMineralCryolite()),
	Ferberite(new MaterialMineralFerberite()),
	Fluorite(new MaterialMineralFluorite()),
	Gypsum(new MaterialMineralGypsum()),
	Hematite(new MaterialMineralHematite()),
	Hubnerite(new MaterialMineralHubnerite()),
	Limestone(new MaterialMineralLimestone()),
	Magnetite(new MaterialMineralMagnetite()),
	Pyrolusite(new MaterialMineralPyrolusite()),
	RockSalt(new MaterialMineralRockSalt()),
	Thorite(new MaterialMineralThorianite()),
	Uranite(new MaterialMineralUraninite());
    /*, TODO add all Materials
    Constantan(new MaterialMetalConstantan()),
    Electrum(new MaterialMetalElectrum()),
    Steel(new MaterialMetalSteel()) */

	public final Material material;

	EnumMaterials(Material material)
	{
		this.material = material;
	}

	public static Stream<EnumMaterials> filterMetals()
	{
		return Stream.of(values()).filter(enumMaterials -> enumMaterials.material.getMaterialType()==MaterialTypes.METAL);
	}

	public static Stream<EnumMaterials> filterStones()
	{
		return Stream.of(values()).filter(enumMaterials -> enumMaterials.material.getMaterialType()==MaterialTypes.STONE);
	}

	public static Stream<EnumMaterials> filterMinerals()
	{
		return Stream.of(values()).filter(enumMaterials -> enumMaterials.material.getMaterialType()==MaterialTypes.MINERAL);
	}

}
