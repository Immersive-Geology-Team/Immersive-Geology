package com.igteam.immersive_geology.api.materials.material_data.metals;

import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by Crimson on 31-03-2020.
 */
public class MaterialMetalTin extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "tin";
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
				new ElementProportion(PeriodicTableElement.TIN)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2875;
	}

	@Override
	public int getMeltingPoint()
	{
		return 505;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0xd3d4d5;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 1.0F;
	}

	@Override
	public float getMiningResistance()
	{
		return 2.0F;
	}

	@Override
	public float getBlastResistance()
	{
		return 1;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 0.6f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 1;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.METAL;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.TETRAGONAL;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
