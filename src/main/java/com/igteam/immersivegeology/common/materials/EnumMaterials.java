package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.crystals.*;
import com.igteam.immersivegeology.common.materials.fluids.MaterialFluidBrine;
import com.igteam.immersivegeology.common.materials.fluids.MaterialFluidCreosote;
import com.igteam.immersivegeology.common.materials.fluids.MaterialFluidWater;
import com.igteam.immersivegeology.common.materials.layer.MaterialLayerMoss;
import com.igteam.immersivegeology.common.materials.metals.*;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalConstantan;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalElectrum;
import com.igteam.immersivegeology.common.materials.metals.alloys.MaterialMetalSteel;
import com.igteam.immersivegeology.common.materials.minerals.*;
import com.igteam.immersivegeology.common.materials.stones.*;
import com.igteam.immersivegeology.common.materials.wood.*;

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
	Zinc(new MaterialMetalZinc()),

	Constantan(new MaterialMetalConstantan()),
	Electrum(new MaterialMetalElectrum()),
	Steel(new MaterialMetalSteel()),

	//Crystals
	Anthracite(new MaterialCrystalAnthracite()),
	Bitumen(new MaterialCrystalBituminous()),
	Diamond(new MaterialCrystalDiamond()),
	Graphite(new MaterialCrystalGraphite()),
	Lignite(new MaterialCrystalLignite()),
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

	//Minerals that spawn in such a large quantity that we don't need an 'orebearing' variant, this is where most clay types will end up!
	Kaolinite(new MaterialMineralKaolinite()),


	//Fluids
	Water(new MaterialFluidWater()),
	Brine(new MaterialFluidBrine()),
	Creosote(new MaterialFluidCreosote()),

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
	Pegmatite(new MaterialStonePegmatite()),
	Regolith(new MaterialStoneRegolith()),

	//Wood
	// TODO: 22.08.2020 add more types
	Oak(new MaterialWoodOak()),
	Birch(new MaterialWoodBirch()),
	Spruce(new MaterialWoodSpruce()),
	Jungle(new MaterialWoodJungle()),
	Acacia(new MaterialWoodAcacia()),
	DarkOak(new MaterialWoodDarkOak()),
	Ebony(new MaterialWoodEbony()),
	Beech(new MaterialWoodBeech()),
	TreatedWood(new MaterialTreatedWood()),

	//Layer Types
	Moss(new MaterialLayerMoss());

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
	 * @return materials used by worldgen (must have a dummy GENERATED_ORE subtype)
	 */
	public static Stream<EnumMaterials> filterWorldGen()
	{
		return filterByUseType(MaterialUseType.GENERATED_ORE);
	}

	/**
	 * @return only materials of a type (metal, mineral, fluid, etc.)
	 */
	public static Stream<EnumMaterials> filterByUseType(MaterialUseType type)
	{
		return Stream.of(values()).filter(enumMaterials -> (enumMaterials.material.hasUsetype(type)));
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

	public static EnumMaterials filterByName(String name)
	{
		if(name.isEmpty())
			return Empty;
		return Stream.of(values()).filter(enumMaterials -> (enumMaterials.material.getName().equals(name))).findFirst().orElse(Empty);
	}
}
