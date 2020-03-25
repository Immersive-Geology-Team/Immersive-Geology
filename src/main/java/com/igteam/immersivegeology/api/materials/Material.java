package com.igteam.immersivegeology.api.materials;

import com.igteam.immersivegeology.ImmersiveGeology;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class Material
{

	/**
	 * @return material name
	 */
	public abstract String getName();

	/**
	 * @return material mod ID
	 */
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	public abstract boolean isFromPeriodicTable();

	public abstract PeriodicTableElement getElement();

	public abstract Rarity getRarity();

	//Block Properties
	public abstract float getHardness();

	public abstract float getMiningResistance();

	public abstract float getBlastResistance();

	public abstract float getDensity();

	public abstract int getBlockMiningLevel();

	public abstract net.minecraft.block.material.Material getBlockMaterial();

	public boolean hasSubtype(MaterialUseType useType)
	{
		return true;
	}

	@Nullable
	public ResourceLocation getSpecialSubtypeTexture(MaterialUseType useType)
	{
		return null;
	}

	//Item Properites
	public abstract float getDurability();

	public abstract float getMiningSpeed();

	public abstract int getItemMiningLevel();

}
