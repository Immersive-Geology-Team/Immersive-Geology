package com.igteam.immersive_geology.api.materials.material_data.metals.alloys;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalBronze extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "bronze";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.ALLOY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.COPPER,3),
				new ElementProportion(PeriodicTableElement.TIN)));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2573;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1183; //TODO could not find exact melting point, chose a value between gold and silver leaning more to silvers
	}

	@Override
	public int getColor(int temperature)
	{
		return 0xb56d46;
	}

	@Override
	public float getHardness()
	{
		return 2.45f;
	}

	@Override
	public float getMiningResistance()
	{
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		return 8;
	}

	@Override
	public float getDensity()
	{
		return 13.5f; //gm/cm^3
	}
}
