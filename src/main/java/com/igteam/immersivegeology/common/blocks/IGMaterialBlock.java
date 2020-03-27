package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import net.minecraft.block.Block;
import net.minecraft.state.IProperty;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialBlock extends IGBaseBlock
{

	public IGMaterialBlock(Material material, MaterialUseType type, Block.Properties blockProps, IProperty... additionalProperties)
	{
		super(material.getName()+"_"+type.getName(), blockProps, IGBlockItem.class, additionalProperties);
	}


}
