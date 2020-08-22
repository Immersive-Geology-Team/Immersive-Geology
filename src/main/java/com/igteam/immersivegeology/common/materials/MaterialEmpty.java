package com.igteam.immersivegeology.common.materials;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;

/**
 * Created by Pabilo8 on 25-03-2020.
 * A material (or actually lack of one) for compatibility, less crashing if a material has been removed
 */
public class MaterialEmpty extends Material
{
	@Override
	public String getName()
	{
		return "missingno";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>();
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.EPIC;
	}

	@Nonnull
	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.STONE;
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
