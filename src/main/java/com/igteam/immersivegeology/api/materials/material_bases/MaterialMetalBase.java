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
				return hasIngot();
			case PLATE:
				return hasPlate();
			case DUST:
				return hasDust();
			case TINY_DUST:
				return hasTinyDust();
			case ROD:
				return hasRod();
			case GEAR:
				return hasGear();
			case STORAGE:
				return hasStorageBlock();
			case SHEETMETAL:
				return hasSheetmetal();
			case DUST_BLOCK:
				return hasDustBlock();
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

	public boolean hasIngot() { return true; };
	public boolean hasPlate() { return true; };
	public boolean hasRod() { return true; };
	public boolean hasGear() { return true; };
	public boolean hasSheetmetal() { return true; };
	public boolean hasDust() { return true; };
	public boolean hasTinyDust() { return true; };
	public boolean hasStorageBlock() { return true; };
	public boolean hasDustBlock() { return true; };
}
