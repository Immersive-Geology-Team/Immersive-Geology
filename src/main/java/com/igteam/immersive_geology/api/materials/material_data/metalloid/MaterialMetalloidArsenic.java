package com.igteam.immersive_geology.api.materials.material_data.metalloid;

import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalloidBase;
import com.igteam.immersive_geology.core.config.IGOreConfig;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalloidArsenic extends MaterialMetalloidBase
{
	@Override
	public String getName()
	{
		return "arsenic";
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
		return EnumMetalType.METALLOID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.ARSENIC)
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
		return 887;
	}

	@Override
	public int getMeltingPoint()
	{
		// TODO Auto-generated method stub
		return 817;
	}

	@Override
	public int getColor(int temperature)
	{
		// TODO Auto-generated method stub
		return 0x3B444B;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 5.73f;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.HEXAGONAL;
	}

	@Override
	public boolean isNativeMetal() {return true;}
}
