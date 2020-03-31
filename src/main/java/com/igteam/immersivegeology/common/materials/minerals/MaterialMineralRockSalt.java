package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralRockSalt extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "rock salt";
	}

	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public Set<PeriodicTableElement.ElementProportion> getElements()
	{
		return new HashSet<>(
				Arrays.asList(
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
				)
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public EnumMineralType getMineralType() {
		return EnumMineralType.MINERAL;
	}

	@Override
	public int getBoilingPoint()
	{
		return -1;
	}

	@Override
	public int getMeltingPoint()
	{
		return -1;
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

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return null;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
