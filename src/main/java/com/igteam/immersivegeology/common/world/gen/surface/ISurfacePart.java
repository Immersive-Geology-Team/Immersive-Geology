package com.igteam.immersivegeology.common.world.gen.surface;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.gen.surface.util.RockData;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public interface ISurfacePart {
	
	
	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart grass() {
		// TODO use x z and a dirt data to find the correct dirt types for biomes!
		return (x, z) -> Blocks.GRASS_BLOCK.getDefaultState();
	}
	
	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart dirt() {
		// TODO use x z and a dirt data to find the correct dirt types for biomes!
		return (x, z) -> Blocks.DIRT.getDefaultState();
	}
	
	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart sand() {
		// TODO use x z and a dirt data to find the correct dirt types for biomes!
		return (x, z) -> Blocks.SAND.getDefaultState();
	}

	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart gravel() {
		// TODO use x z and a dirt data to find the correct dirt types for biomes!
		return (x, z) -> Blocks.GRAVEL.getDefaultState();
	}
	
	@Nonnull
    @SuppressWarnings("ConstantConditions")
	static ISurfacePart clay() {
		// TODO use x z and a dirt data to find the correct dirt types for biomes!
		return (x, z) -> Blocks.CLAY.getDefaultState();
	}
	
	BlockState get(int x, int z);
}  
