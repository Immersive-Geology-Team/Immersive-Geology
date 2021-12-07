package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;

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
			case NUGGET:
				return hasNugget();
			case PLATE:
				return hasPlate();
			case ROD:
				return hasRod();
			case GEAR:
				return hasGear();
			case WIRE:
				return hasWire();
			case DUST:
			case TINY_DUST:
				return hasDust();
			case SHEETMETAL:
				return hasSheetmetal();
			case SLAB:
				return hasSlab();
			case SHEETMETAL_STAIRS:
				return hasStairs();
			case DUST_BLOCK:
				return hasDustBlock();
			case STORAGE_BLOCK:
				return true;
			case METAL_OXIDE:
				return generateOxide();
			case RAW_CRYSTAL:
				return hasCrystal();
			case ORE_CHUNK:
			case ORE_BIT:
			case DIRTY_CRUSHED_ORE:
			case ORE_STONE:
				return isNativeMetal();
			default:
				return false;
		}
	}

	public boolean generateOxide(){
		return false;// !(isAlloy() || isNativeMetal);
	}

	public boolean isAlloy(){
		return getMetalType() == EnumMetalType.ALLOY;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.METAL;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return (isNativeMetal ? MaterialTypes.MINERAL : MaterialTypes.METAL);
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public enum EnumMetalType
	{
		METALLOID,
		METAL,
		ALLOY
	}

	public boolean hasIngot()
	{
		return true;
	}

	public boolean hasNugget()
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

	public boolean hasSheetmetal()
	{
		return true;
	}

	public boolean hasSlab()
	{
		return true;
	}

	public boolean hasStairs() { return true; }

	public boolean hasDust()
	{
		return true;
	}

	public boolean hasDustBlock()
	{
		return true;
	}

	public boolean hasCrystal(){
		return true;
	}

	public boolean isNativeMetal() {return false;}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}


	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public boolean preExists() {
		return false;
	}

	//Input the processing steps for this material
	@Override
	public IGMaterialProcess getProcessingMethod() {
		return null;
	}

}
