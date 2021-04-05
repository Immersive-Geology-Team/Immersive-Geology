package com.igteam.immersive_geology.api.materials.material_data.metals.alloys;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalElectrum extends MaterialMetalBase
{

	@Override
	public String getName()
	{
		return "electrum";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.GOLD),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILVER)));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 3243;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1656; //TODO could not find exact melting point, chose a value between gold and silver leaning more to silvers
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x94826A;
	}

	@Override
	public float getHardness()
	{
		return 2.75f;
	}

	@Override
	public float getMiningResistance()
	{
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		return 4;
	}

	@Override
	public float getDensity()
	{
		return 13.5f; //gm/cm^3
	}
}
