package com.igteam.immersivegeology.common.world.gen.surface;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.gen.surface.util.RockData;

import net.minecraft.block.BlockState;

public interface ISurfacePart {
	
	
	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart rock() {
		// TODO Auto-generated method stub
		return (rockData, x, z) -> IGBlockGrabber.grabBlock(MaterialUseType.ROCK, rockData.getRock(x,z)).getDefaultState();
	}
	
	BlockState get(RockData rockData, int x, int z);
}  
