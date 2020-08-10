package com.igteam.immersivegeology.common.materials.metals.alloys;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalSteel extends MaterialMetalBase
{

	@Override
	public String getName()
	{
		return "steel";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.ALLOY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
			new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
			new PeriodicTableElement.ElementProportion(PeriodicTableElement.CARBON))
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 3200;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1698;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x717377;
	}

	@Override
	public float getHardness()
	{
		return 3;
	}

	@Override
	public float getMiningResistance()
	{
		return 8;
	}

	@Override
	public float getBlastResistance()
	{
		return 12;
	}

	@Override
	public float getDensity()
	{
		return 7.800f; // gm/cm^3
	}

}
