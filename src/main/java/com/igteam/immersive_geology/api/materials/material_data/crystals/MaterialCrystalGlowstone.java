package com.igteam.immersive_geology.api.materials.material_data.crystals;

import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialCrystalBase;
import com.igteam.immersive_geology.core.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialCrystalGlowstone extends MaterialCrystalBase
{
	@Override
	public String getName()
	{
		return "glowstone";
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
				new ElementProportion(PeriodicTableElement.LUXUM)
		));
	}

	@Nonnull
	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 1573;
	}

	@Override
	public int getMeltingPoint()
	{
		return 923;
	}

	public static int baseColor = 0xFFBC5E;

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
