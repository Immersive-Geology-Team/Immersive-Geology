package com.igteam.immersivegeology.common.blocks.metal;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGSheetmetalBlock extends IGMaterialBlock
{
	public IGSheetmetalBlock(Material material)
	{
		super(material, MaterialUseType.SHEETMETAL);
	}

	//Hardness * 0.6
	private static Properties createPropertyFromMaterial(Material material)
	{
		return Properties.create(material.getBlockMaterial()).hardnessAndResistance(material.getHardness()*0.6f, material.getMiningResistance());
	}
}
