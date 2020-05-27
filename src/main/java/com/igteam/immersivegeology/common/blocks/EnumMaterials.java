package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.common.materials.MaterialEmpty;
import com.igteam.immersivegeology.common.materials.crystals.MaterialCrystalQuartz;
import com.igteam.immersivegeology.common.materials.fluids.MaterialFluidWater;
import com.igteam.immersivegeology.common.materials.metals.*;
import com.igteam.immersivegeology.common.materials.stones.*;
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
	Uranium(new MaterialMetalUranium()),
    /*, TODO add all Materials
=======
	Tin(new MaterialMetalTin()),
	Titanium(new MaterialMetalTitanium()),
	Tungsten(new MaterialMetalTungsten()),
	Vanadium(new MaterialMetalVanadium()),
	Zirconium(new MaterialMetalZirconium()),
	/* TODO add alloys

    Constantan(new MaterialMetalConstantan()),
    Electrum(new MaterialMetalElectrum()),
    Steel(new MaterialMetalSteel()), */

	//Crystals
	Quartz(new MaterialCrystalQuartz()),

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
	Magnetite(new MaterialMineralMagnetite()),
	Pyrolusite(new MaterialMineralPyrolusite()),
	RockSalt(new MaterialMineralRockSalt()),
	Thorite(new MaterialMineralThorianite()),
	Uraninite(new MaterialMineralUraninite()),

	//Fluids
	Water(new MaterialFluidWater()),

	//Stones
	//TODO: add chemical formulas for the stones
	Rhyolite(new MaterialStoneRhyolite()),
	Diorite(new MaterialStoneDiorite()),
	Andesite(new MaterialStoneAndesite()),
	Granite(new MaterialStoneGranite()),
	Netherrack(new MaterialStoneNetherrack()),
	Endstone(new MaterialStoneEndstone()),
	Basalt(new MaterialStoneBasalt()),
	Limestone(new MaterialStoneLimestone());

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
