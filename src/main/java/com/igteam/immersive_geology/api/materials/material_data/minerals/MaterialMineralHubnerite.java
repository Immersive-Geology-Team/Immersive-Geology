package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralHubnerite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "hubnerite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 6202;
	}

	@Override
	public int getMeltingPoint()
	{
		return 3695;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}


	public static int baseColor = 0x32332E;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 0;
	}

	@Override
	public float getMiningResistance()
	{
		return 0;
	}

	@Override
	public float getBlastResistance()
	{
		return 0;
	}

	@Override
	public float getDensity()
	{
		return 0;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.MONOCLINIC;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Tungsten;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Manganese;
	}
}