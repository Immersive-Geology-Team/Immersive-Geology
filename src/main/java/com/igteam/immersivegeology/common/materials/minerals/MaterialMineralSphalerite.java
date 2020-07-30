package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralSphalerite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "sphalerite";
	}

	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZINC),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2400;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1100;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.MINERAL;
	}

	public static int baseColor = 0x6F8070;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 1;
	}

	@Override
	public float getMiningResistance()
	{
		return 1;
	}

	@Override
	public float getBlastResistance()
	{
		return 1;
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
}
