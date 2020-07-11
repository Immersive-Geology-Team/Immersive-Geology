package com.igteam.immersivegeology.common.util;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;

import net.minecraft.block.Block;

public class IGBlockGrabber {

	public static IGBaseBlock grabBlock(MaterialUseType usetype, Material mat) {
		return IGContent.registeredIGBlocks.get(usetype.getName() + "_" + mat.getName());
	}
	 
	public static IGBaseBlock grabOreBlock(MaterialUseType usetype, Material mat, EnumOreBearingMaterials oreType) {
		return IGContent.registeredIGBlocks.get(usetype.getName() + "_" + mat.getName() + "_" + oreType.toString().toLowerCase());
	}
} 
