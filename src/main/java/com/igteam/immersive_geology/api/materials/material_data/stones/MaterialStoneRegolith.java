package com.igteam.immersive_geology.api.materials.material_data.stones;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialStoneRegolith extends MaterialStoneBase
{

	@Override
	public String getName()
	{
		return "regolith";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public EnumStoneType getStoneType()
	{
		// TODO Auto-generated method stub
		return EnumStoneType.SEDIMENTARY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(new ElementProportion(PeriodicTableElement.ALUMINIUM)));
	}

	@Override
	public Rarity getRarity()
	{
		// TODO Auto-generated method stub
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMeltingPoint()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColor(int temperature)
	{
		// TODO Auto-generated method stub
		return 0x6B4535;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return .5f;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return .5f;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 6f;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}