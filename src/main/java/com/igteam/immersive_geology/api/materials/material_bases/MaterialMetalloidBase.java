package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialMetalloidBase extends MaterialMetalBase
{
	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.METALLOID;
	}

	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.METALLOID;
	}
	
	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public boolean hasIngot()
	{
		return true;
	}

	public boolean hasPlate()
	{
		return true;
	}

	public boolean hasRod()
	{
		return true;
	}

	public boolean hasGear()
	{
		return true;
	}

	public boolean hasWire()
	{
		return true;
	}

	public boolean hasNugget()
	{
		return true;
	}

	public boolean hasDust()
	{
		return true;
	}

	public boolean hasCrystal(){
		return true;
	}

	@Override
	public boolean preExists() {
		return false;
	} //Used in data gen for crusher

	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}
}
