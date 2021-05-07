package com.igteam.immersive_geology.api.materials.material_bases;

import blusunrize.immersiveengineering.common.config.CachedConfig;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.core.config.IGOreConfig;

public abstract class MaterialCrystalBase extends Material
{
	protected static int baseColor = 0xffffff;

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case GEODE:
			case RAW_CRYSTAL:
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
		return net.minecraft.block.material.Material.GLASS;
	}

	public boolean hasCutCrystal()
	{
		return true;
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


	@Override
	public IGOreConfig getGenerationConfig() {
		return new IGOreConfig(1,1,40,10);
	}

	@Override
	public float getMaxDrops() {
		return 5f;
	}

	@Override
	public float getMinDrops() {
		return 2f;
	}


}
