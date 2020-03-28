package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.common.materials.MaterialEmpty;
import com.igteam.immersivegeology.common.materials.metals.*;
import com.igteam.immersivegeology.common.materials.stones.*;

import java.util.stream.Stream;

public enum EnumMaterials
{

	Empty(new MaterialEmpty()),
	//Metals
	Aluminum(new MaterialMetalAluminium()),
	Copper(new MaterialMetalCopper()),
	Gold(new MaterialMetalGold()),
	Iron(new MaterialMetalIron()),
	Lead(new MaterialMetalLead()),
	Nickel(new MaterialMetalNickel()),
	Silver(new MaterialMetalSilver()),
	Uranium(new MaterialMetalUranium()),
    /*, TODO add all Materials
    Constantan(new MaterialMetalConstantan()),
    Electrum(new MaterialMetalElectrum()),
    Steel(new MaterialMetalSteel()) */

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

	EnumMaterials(Material metal)
	{
		this.material = metal;
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
