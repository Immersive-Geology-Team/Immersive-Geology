package com.igteam.immersive_geology.api.materials.material_data.metals.alloys;

import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalConstantan extends MaterialMetalBase
{

	@Override
	public String getName()
	{
		return "constantan";
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
		// TODO Auto-generated method stub
		return EnumMetalType.ALLOY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL)));
	}

	@Override
	public Rarity getRarity()
	{
		// TODO Auto-generated method stub
		return Rarity.UNCOMMON;
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
		return 0xFE8E70;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 8.885f; // g/cm^3
	}

	@Override
	public boolean hasCrystal(){
		return false;
	}
}
