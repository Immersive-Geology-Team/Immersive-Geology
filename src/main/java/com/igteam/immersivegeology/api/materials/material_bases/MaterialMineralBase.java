package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase.EnumStoneType;

import javax.annotation.Nullable;

public abstract class MaterialMineralBase extends Material
{
	public abstract EnumMineralType getMineralType();

	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		switch(useType)
		{
			case GENERATED_ORE:
			case GENERATED_CHUNKS:
			case DUST:
			case TINY_DUST:
				return true;
		}
		return false;
	}

	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.ROCK)
			return EnumStoneType.SEDIMENTARY.getName();
		return null;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.MINERAL;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.ROCK;
	}

	public int getStaticColor()
	{
		return 0xffffff;
	}

	public enum EnumMineralType
	{
		MINERAL
	}

}
