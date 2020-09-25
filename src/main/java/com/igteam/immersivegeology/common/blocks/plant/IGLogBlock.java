package com.igteam.immersivegeology.common.blocks.plant;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGLogBlock extends IGMaterialBlock
{
	public IGLogBlock(Material material)
	{
		super(MaterialUseType.LOG, material);
	}

	//No changes to stats
	private static Properties createPropertyFromMaterial(Material material)
	{
		return Properties.create(material.getBlockMaterial()).hardnessAndResistance(material.getHardness(), material.getMiningResistance());
	}
}
