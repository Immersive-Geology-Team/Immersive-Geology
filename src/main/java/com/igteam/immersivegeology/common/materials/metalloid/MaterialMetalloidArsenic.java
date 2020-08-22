package com.igteam.immersivegeology.common.materials.metalloid;

import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalloidBase;
import net.minecraft.item.Rarity;

import java.util.LinkedHashSet;

public class MaterialMetalloidArsenic extends MaterialMetalloidBase
{

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.METALLOID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return null;
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 887;
	}

	@Override
	public int getMeltingPoint()
	{
		return 817;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x3B444B;
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
		return 5.73f;
	}

}
