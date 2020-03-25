package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public class MaterialMetalCopper extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "copper";
	}

	@Override
	public Set<ElementProportion> getElements()
	{
		return new HashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.COPPER)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public float getBoilingPoint()
	{
		return 2835f;
	}

	@Override
	public float getMeltingPoint()
	{
		return 1357.77f;
	}

	@Override
	public int getColor()
	{
		return 0xe39919;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 5.0F;
	}

	@Override
	public float getMiningResistance()
	{
		return 10.0F;
	}

	@Override
	public float getBlastResistance()
	{
		return 6;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 1.25f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 1;
	}


	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
