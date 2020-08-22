package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralIlmenite extends MaterialMineralBase
{

	@Override
	public String getName()
	{
		return "ilmenite";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}


	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.MINERAL;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 3923;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1923;
	}

	public static int baseColor = 0x4A3E3E;

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
		return 4.55f;
	}
}
