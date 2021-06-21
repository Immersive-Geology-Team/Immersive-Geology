package com.igteam.immersive_geology.api.materials.material_bases;

import javax.annotation.Nullable;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
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
			case RAW_CRYSTAL:
			case CUT_CRYSTAL:
				return hasCrystal();
			case ORE_STONE:
			case ORE_BIT:
			case ORE_CHUNK:
			case ORE_CRUSHED:
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

	protected boolean hasCrystal(){
		return getMineralType() == EnumMineralType.CRYSTAL;
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
		NATIVE,
		CRYSTAL,
		FUEL,
		CLAY
	}

	public int getStaticColor()
	{
		return 0xffffff;
	}

	public MaterialEnum getProcessedType(){
		return null;
	}

	@Override
	public boolean preExists() {
		return false;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}
}
