package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.*;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

/**
 * Created by JStocke12 on 31-03-2020.
 */
public abstract class MaterialFluidBase extends Material
{
	public abstract EnumFluidType getFluidType();

	protected boolean isSolid = false;

	protected boolean useDefaultFluidTextures = true;

	public abstract Set<PeriodicTableElement.ElementProportion> getSoluteElements();

	public abstract float getConcentration();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case BUCKET:
				return hasBucket();
			case FLASK:
				return hasFlask();
			case FLUIDS:
				return hasFluidBlock();
		}
		return false;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.FLUID;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.FLUID;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.WATER;
	}

	public enum EnumFluidType
	{
		FLUID(0),
		SOLUTION(1),
		GAS(2);

		private int type;

		EnumFluidType(int type){
			this.type = type;
		}

		public boolean isGas() {
			return type == 2;
		}
	}

	public boolean hasBucket()
	{
		return false;
	}

	public boolean hasFluidBlock()
	{
		return true;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}


	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	public Effect getContactEffect(){
		return null;
	}

	public int getContactEffectDuration(){
		return 0;
	}

	public int getContactEffectLevel(){
		return 0;
	}

	public ResourceLocation getFluidStill(){
		if(useDefaultFluidTextures){
			return new ResourceLocation(IGLib.MODID + ":block/" + getTypeName().toLowerCase() + "/" + "default" + "_still");
		}
		return new ResourceLocation(IGLib.MODID + ":block/"  + getTypeName().toLowerCase() + "/" + getName() + "_still");
	}

	public ResourceLocation getFluidFlowing(){
		if(useDefaultFluidTextures){
			return new ResourceLocation(IGLib.MODID + ":block/" + getTypeName().toLowerCase() + "/" + "default" + "_flow");
		}
		return new ResourceLocation(IGLib.MODID + ":block/" + getTypeName().toLowerCase() + "/" + getName() + "_flow");
	}

	String getTypeName(){
		return getFluidType().toString().toLowerCase();
	}

	@Override
	public boolean preExists() {
		return false;
	}

	public boolean getIsSolid(){
		return isSolid;
	}

	public boolean hasFlask() {
		return false;
	}
}
