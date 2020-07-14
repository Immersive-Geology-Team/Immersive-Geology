package com.igteam.immersivegeology.common.blocks.metal;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGDustBlock extends IGMaterialBlock
{
	public IGDustBlock(Material material)
	{
		super(material, MaterialUseType.DUST);
	}

	//Hardness * 0.4, Mining resistance * 0.5
	private static Properties createPropertyFromMaterial(Material material)
	{
		return Properties.create(material.getBlockMaterial()).hardnessAndResistance(material.getHardness()*0.4f, material.getMiningResistance()*0.5f);
	}
}
