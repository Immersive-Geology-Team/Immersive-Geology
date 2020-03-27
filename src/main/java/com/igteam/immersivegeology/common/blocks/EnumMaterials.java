package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.common.materials.MaterialEmpty;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;

import java.util.stream.Stream;

public enum EnumMaterials
{

	Empty(new MaterialEmpty()),
	Copper(new MaterialMetalCopper())
    /*, TODO add all Materials
    Aluminum(new MaterialMetalAluminum(), Type.IE_PURE),
    Lead(new MaterialMetalLead(), Type.IE_PURE),
    Silver(new MaterialMetalSilver(), Type.IE_PURE),
    Nickel(new MaterialMetalNickel(), Type.IE_PURE),
    Uranium(new MaterialMetalUranium(), Type.IE_PURE),
    Constantan(new MaterialMetalConstantan(), Type.IE_ALLOY),
    Electrum(new MaterialMetalElectrum(), Type.IE_ALLOY),
    Steel(new MaterialMetalSteel(), Type.IE_ALLOY),
    Iron(new MaterialMetalIron(), Type.VANILLA),
    Gold(new MaterialMetalGold(), Type.VANILLA) */;

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
