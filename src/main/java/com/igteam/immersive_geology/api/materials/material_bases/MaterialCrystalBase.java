package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;

public abstract class MaterialCrystalBase extends Material
{
	protected static int baseColor = 0xffffff;

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case CRYSTAL:
				return hasCrystal();
			case CUT_CRYSTAL:
				return hasCutCrystal();
			case DUST:
				return hasDust();
			case DUST_BLOCK:
				return hasDustBlock();
		}
		return false;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.CRYSTAL;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.CRYSTAL;
	}
	
	public static int getStaticColor()
	{
		return baseColor;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.STONE;
	}

	public boolean hasCutCrystal()
	{
		return false;
	}

	public boolean hasCrystal()
	{
		return true;
	}

	public boolean hasDust()
	{
		return true;
	}

	public boolean hasDustBlock()
	{
		return true;
	}
}
