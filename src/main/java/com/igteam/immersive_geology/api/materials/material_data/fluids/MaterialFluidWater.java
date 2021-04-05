package com.igteam.immersive_geology.api.materials.material_data.fluids;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.MaterialTypes;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import net.minecraft.item.Rarity;
import com.igteam.immersive_geology.core.IGLib;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialFluidWater extends MaterialFluidBase
{
	@Override
	public String getName()
	{
		return "water";
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
				new ElementProportion(PeriodicTableElement.HYDROGEN, 2),
				new ElementProportion(PeriodicTableElement.OXYGEN)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public EnumFluidType getFluidType()
	{
		return EnumFluidType.FLUID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getSoluteElements()
	{
		return null;
	}

	@Override
	public float getConcentration()
	{
		return 0;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.FLUID;
	}

	@Override
	public int getBoilingPoint()
	{
		return 373;
	}

	@Override
	public int getMeltingPoint()
	{
		return 273;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0xffffff;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return -1F;
	}

	@Override
	public float getMiningResistance()
	{
		return -1F;
	}

	@Override
	public float getBlastResistance()
	{
		return -1F;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 1f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}
}
