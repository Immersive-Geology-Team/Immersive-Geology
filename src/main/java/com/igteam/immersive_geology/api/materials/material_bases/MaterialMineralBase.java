package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase.EnumStoneType;

import javax.annotation.Nullable;

public abstract class MaterialMineralBase extends Material
{
	public abstract EnumMineralType getMineralType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case CRUSHED_ORE:
				return hasCrushedOre();
			case ORE_STONE:
			case ORE_BIT:
			case ORE_CHUNK:
			case DIRTY_CRUSHED_ORE:
				return true;
			case TINY_DUST:
			case DUST:
				return hasDust();
			case CLAY:
				return hasClay();
			case SLURRY:
				return hasSlurry();
		}
		return false;
	}

	public boolean hasCrushedOre(){
		return true;
	}

	public boolean hasDust(){
		return true;
	}

	public boolean hasClay(){
		return getMineralType() == EnumMineralType.CLAY;
	}

	public boolean isOxide(){
		boolean result = false;

		int element_length = getElements().size();

		if(element_length == 2){
			for(PeriodicTableElement.ElementProportion e : getElements()){
				if(e.getElement().getSymbol().equals(PeriodicTableElement.OXYGEN.getSymbol())) {
					result = true;
				}
			}
		}

		return result;
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

	//Defaults to Iron
	@Override
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Iron;
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
