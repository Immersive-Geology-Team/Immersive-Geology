package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.common.materials.crystals.MaterialCrystalQuartz;
import com.igteam.immersivegeology.common.materials.fluids.MaterialFluidWater;
import com.igteam.immersivegeology.common.materials.metals.*;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalConstantan;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalElectrum;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalSteel;
import com.igteam.immersivegeology.common.materials.minerals.*;
import com.igteam.immersivegeology.common.materials.stones.*;

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
	Cobalt(new MaterialMetalCobalt()),
	Uranium(new MaterialMetalUranium()),
	Tin(new MaterialMetalTin()),
	Titanium(new MaterialMetalTitanium()),
	Tungsten(new MaterialMetalTungsten()),
	Vanadium(new MaterialMetalVanadium()),
	Zirconium(new MaterialMetalZirconium()),

	Constantan(new MaterialMetalConstantan()),
	Electrum(new MaterialMetalElectrum()),
	Steel(new MaterialMetalSteel()),

	//Crystals
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
	Gabbros(new MaterialStoneGabbros()),
	Marble(new MaterialStoneMarble()),
	Limestone(new MaterialStoneLimestone()),
	Pegamite(new MaterialStonePegamite()),
	Regolith(new MaterialStoneRegolith());

	public final Material material;

	EnumMaterials(Material material)
	{
		this.material = material;
	}

	/**
	 * @return only metal materials
	 */
	public static Stream<EnumMaterials> filterMetals()
	{
		return filterByType(MaterialTypes.METAL);
	}

	/**
	 * @return only stone materials
	 */
	public static Stream<EnumMaterials> filterStones()
	{
		return filterByType(MaterialTypes.STONE);
	}

	/**
	 * @return only mineral materials
	 */
	public static Stream<EnumMaterials> filterMinerals()
	{
		return filterByType(MaterialTypes.MINERAL);
	}
	
	/**
	 * @return only mineral materials
	 */
	public static Stream<EnumMaterials> filterWorldGen()
	{
		return filterBySubType(MaterialTypes.MINERAL); //some metals have mineral as a subtype, (reusing mineral type in place of adding in a native material type)
	}
	
	public static Stream<EnumMaterials> filterBySubType(MaterialTypes type)
	{
		return Stream.of(values()).filter(enumMaterials -> (enumMaterials.material.getMaterialSubType()==type));
	}

	/**
	 * @return only materials of a type (metal, mineral, fluid, etc.)
	 */
	public static Stream<EnumMaterials> filterByType(MaterialTypes type)
	{
		return Stream.of(values()).filter(enumMaterials -> (enumMaterials.material.getMaterialType()==type));
	}

	/**
	 * @return materials of one of the types
	 */
	public static Stream<EnumMaterials> filterByTypes(MaterialTypes... types)
	{
		Stream<MaterialTypes> typesStream = Stream.of(types);
		return Stream.of(values()).filter(enumMaterials -> typesStream.anyMatch(materialTypes -> enumMaterials.material.getMaterialType()==materialTypes));
	}
}
