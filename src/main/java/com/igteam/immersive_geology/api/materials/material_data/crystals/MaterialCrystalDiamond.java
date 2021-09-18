package com.igteam.immersive_geology.api.materials.material_data.crystals;

import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialCrystalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialCrystalDiamond extends MaterialCrystalBase
{
	@Override
	public String getName()
	{
		return "diamond";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.CARBON)
		));
	}

	@Nonnull
	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2503;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1923;
	}

	public static int baseColor = 0x8FE0C8;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 3;
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
		return 0.45f;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 1;
	}
}
