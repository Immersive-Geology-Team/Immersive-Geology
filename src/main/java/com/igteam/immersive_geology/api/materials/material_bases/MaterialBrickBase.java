package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialBrickBase extends Material
{
	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			//items
			case BRICK:
			case BRICKS:
				return true;
			case CLAY:
				return hasClay();
		}
		return false;
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
		return net.minecraft.block.material.Material.ROCK;
	}

	public boolean hasClay() { return true; }

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
