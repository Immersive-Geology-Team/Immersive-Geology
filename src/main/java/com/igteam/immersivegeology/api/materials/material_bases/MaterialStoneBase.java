package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialStoneBase extends Material
{
	public abstract EnumStoneType getStoneType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			//items
			case ROCK:
				//blocks
			case ORE_BEARING_ROCK:
			case ORE_CHUNK:
			case GRAVEL:
			case COBBLESTONE:
			case POLISHED_STONE:
			case SMALL_BRICKS:
			case NORMAL_BRICKS:
			case ROAD_BRICKS:
			case ROUGH_BRICKS:
			case PILLAR:
			case COLUMN:
			case TILES:
			case CHUNK:
				return true;
		}
		return false;
	}

	@Override
	public MaterialTypes getMaterialType()
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
		SEDIMENTARY;

		public String getName()
		{
			// TODO Auto-generated method stub
			return toString().toLowerCase();
		}
	}
}
