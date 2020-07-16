package com.igteam.immersivegeology.common.blocks.metal;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import net.minecraft.block.Block;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGStorageBlock extends IGMaterialBlock
{
	public IGStorageBlock(Material material)
	{
		super(material, MaterialUseType.STORAGE);
	}

	//No changes to stats
	private static Properties createPropertyFromMaterial(Material material)
	{
		return Block.Properties.create(material.getBlockMaterial()).hardnessAndResistance(material.getHardness(), material.getMiningResistance());
	}
}
