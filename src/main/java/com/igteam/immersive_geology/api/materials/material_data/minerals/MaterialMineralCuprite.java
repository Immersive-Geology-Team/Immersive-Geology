package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralCuprite extends MaterialMineralBase
{

	@Override
	public String getName()
	{
		return "cuprite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(
				Arrays.asList(
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
				)
		);
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
		return 2835;
	}

	@Override
	public int getMeltingPoint()
	{
		// TODO Auto-generated method stub
		return 1357;
	}

	public static int baseColor = 0x830922;

	@Override
	public int getColor(int temperature)
	{
		// TODO Auto-generated method stub
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 8.96f;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		// TODO Auto-generated method stub
		return EnumMineralType.CRYSTAL;
	}
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Copper;
	}
}
