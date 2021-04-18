package com.igteam.immersive_geology.api.materials.material_data.metals.alloys;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalVikingSteel extends MaterialMetalBase
{

	@Override
	public String getName()
	{
		return "viking_steel";
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
			new ElementProportion(PeriodicTableElement.IRON, 10),
			new ElementProportion(PeriodicTableElement.CARBON),
			new ElementProportion(PeriodicTableElement.CALCIUM, 3))
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 3200;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1698;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x6e7064;
	}

	@Override
	public float getHardness()
	{
		return 2.65f;
	}

	@Override
	public float getMiningResistance()
	{
		return 7.5f;
	}

	@Override
	public float getBlastResistance()
	{
		return 11.5f;
	}

	@Override
	public float getDensity()
	{
		return 7.950f; // gm/cm^3
	}

}
