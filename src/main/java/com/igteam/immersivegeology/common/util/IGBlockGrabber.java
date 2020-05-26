package com.igteam.immersivegeology.common.util;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;

import net.minecraft.block.Block;

public class IGBlockGrabber {

	public static IGBaseBlock grabBlock(MaterialUseType usetype, Material mat) {
		return IGContent.registeredIGBlocks.get(usetype.getName() + "_" + mat.getName());
	}
}
