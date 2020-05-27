package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

/**
 * Created by Pabilo8 on 28-03-2020.
 */
public class IGMaterialRockItem extends IGMaterialResourceItem
{

	public IGMaterialRockItem(Material mat, MaterialUseType type)
	{
		super(mat, type);
	}

	/*
	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		if(pass==0)
			return getStoneMaterial().getColor();
		else
			return getMineralMaterial().getColor();
	}
	 */
}
