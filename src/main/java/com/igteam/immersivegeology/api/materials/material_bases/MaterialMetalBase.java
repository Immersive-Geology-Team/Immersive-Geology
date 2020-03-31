package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialMetalBase extends Material
{
	public abstract EnumMetalType getMetalType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case INGOT:
			case PLATE:
			case DUST:
			case TINY_DUST:
			case ROD:
			case GEAR:
			case STORAGE:
			case POOR_ORE:
			case NORMAL_ORE:
			case RICH_ORE:
			case SHEETMETAL:
			case DUST_BLOCK:
			case FLUIDS:
				return true;
		}
		return false;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.METAL;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public enum EnumMetalType
	{
		METAL,
		ALLOY
	}
}
