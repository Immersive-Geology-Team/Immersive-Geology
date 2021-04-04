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
			case STONE:
				return hasRock();
			case DUST:
				return true;
		}
		return false;
	}
	
	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.STONE)
			return EnumStoneType.SEDIMENTARY.getName();
		return null;
	}

	private boolean hasRock() {
		// TODO Auto-generated method stub
		return getMaterialSubType() == MaterialTypes.STONE ? true : false;
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
		return net.minecraft.block.material.Material.STONE;
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
