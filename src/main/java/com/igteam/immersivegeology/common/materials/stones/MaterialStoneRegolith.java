package com.igteam.immersivegeology.common.materials.stones;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
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
		return ImmersiveGeology.MODID;
	}

	@Override
	public EnumStoneType getStoneType()
	{
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
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 0;
	}

	@Override
	public int getMeltingPoint()
	{
		return 0;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x6B4535;
	}

	@Override
	public float getHardness()
	{
		return .5f;
	}

	@Override
	public float getMiningResistance()
	{
		return .5f;
	}

	@Override
	public float getBlastResistance()
	{
		return 6f;
	}

	@Override
	public float getDensity()
	{
		return 1;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

}