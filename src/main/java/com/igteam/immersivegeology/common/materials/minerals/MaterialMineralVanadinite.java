package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralVanadinite extends MaterialMineralBase
{

	@Override
	public String getName()
	{
		return "vanadinite";
	}

	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(
				Arrays.asList(
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD, 5),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.VANADIUM),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
				)
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 7835;
	}

	@Override
	public int getMeltingPoint()
	{
		return 3470;
	}

	public static int baseColor = 0xEF2161;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 2;
	}

	@Override
	public float getMiningResistance()
	{
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		return 2;
	}

	@Override
	public float getDensity()
	{
		return 8.96f;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.MINERAL;
	}

}
