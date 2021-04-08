package com.igteam.immersive_geology.api.materials.material_bases;

import javax.annotation.Nullable;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase.EnumStoneType;

public abstract class MaterialMineralBase extends Material
{
	public abstract EnumMineralType getMineralType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case ORE_STONE:
			case ORE_BIT:
			case ORE_CHUNK:
				return true;
		}
		return false;
	}
	
	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.ORE_STONE)
			return EnumStoneType.SEDIMENTARY.getName();
		return null;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.MINERAL;
	}

	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.MINERAL;
	}
	
	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.ROCK;
	}

	public enum EnumMineralType
	{
		MINERAL
	}

	public int getStaticColor()
	{
		return 0xffffff;
	}

}
