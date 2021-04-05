package com.igteam.immersive_geology.api.materials.material_data.stones;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialStonePegmatite extends MaterialStoneBase
{

	@Override
	public String getName()
	{
		return "pegmatite";
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
		return EnumStoneType.IGNEOUS_INTRUSIVE;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.SILICON),
				new ElementProportion(PeriodicTableElement.OXYGEN,2),
				new ElementProportion(PeriodicTableElement.ALUMINIUM,2),
				new ElementProportion(PeriodicTableElement.OXYGEN,3)
		));
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
		return 0xA78D5C;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return 1.5f;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return 1.5f;
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
