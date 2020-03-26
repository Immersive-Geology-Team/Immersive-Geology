package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialItem extends IGBaseItem
{
	Material material;
	MaterialUseType subtype;

	public IGMaterialItem(Material material, MaterialUseType type)
	{
		super(type.getName()+"_"+material.getName());
		this.subtype = type;
		this.material = material;
	}
}
