package com.igteam.immersivegeology.common.world.gen.surface.util;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;

import net.minecraft.block.Block;

public class RockData {

	public RockData() {
		
	}
	
	public Block getBlock(int x, int z) {
		return IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material);
	}

	public Material getRock(int x, int z) {
		// TODO Auto-generated method stub
		return EnumMaterials.Rhyolite.material;
	}
	
}
