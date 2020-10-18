package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialCrystalStructure;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

public abstract class MaterialCrystalBase extends Material
{
	public abstract MaterialCrystalStructure getCrystalStructure();
	public abstract MaterialCrystalStructure.LatticeStructure getLatticeStructure();
	protected static int baseColor = 0xffffff;

	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		switch(useType)
		{
			case CRYSTAL:
				return hasCrystal();
			case RAW_CRYSTAL:
				return hasRawCrystal();
			case DUST:
				return hasDust();
			case TINY_DUST:
				return hasTinyDust();
			case STORAGE_BLOCK:
				return hasStorageBlock();
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
	
	public static int getStaticColor()
	{
		return baseColor;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.ROCK;
	}

	public boolean hasRawCrystal()
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

	public boolean hasTinyDust()
	{
		return true;
	}

	public boolean hasStorageBlock()
	{
		return true;
	}

	public boolean hasDustBlock()
	{
		return true;
	}
}
