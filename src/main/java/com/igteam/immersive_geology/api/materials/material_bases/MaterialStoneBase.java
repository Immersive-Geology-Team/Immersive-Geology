package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;

import javax.annotation.Nullable;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialStoneBase extends Material
{
	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			//items
			case STONE:
			case COBBLESTONE:
			case CHUNK:
			case ROCK_BIT:
				return true;
		}
		return false;
	}

	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.ORE_STONE||useType==MaterialUseType.STONE)
			return getStoneType().getName();
		return null;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.STONE;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.STONE;
	}
	
	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public enum EnumStoneType
	{
		IGNEOUS_INTRUSIVE,
		IGNEOUS_EXTRUSIVE,
		METAMORPHIC,
		SEDIMENTARY,
		VANILLA;

		public String getName()
		{
			return toString().toLowerCase();
		}
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public boolean preExists() {
		return false;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}
}
