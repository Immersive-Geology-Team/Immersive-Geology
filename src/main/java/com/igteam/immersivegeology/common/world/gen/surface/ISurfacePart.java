package com.igteam.immersivegeology.common.world.gen.surface;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public interface ISurfacePart
{

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart grass()
	{
		return (data, x, z, rainfall, temp) -> data.getGrassBlock(x, z, rainfall, temp);
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart dirt()
	{
		return (data, x, z, rainfall, temp) -> data.getDirtBlock(x, z, rainfall, temp);
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart sand()
	{
		return (data, x, z, rainfall, temp) -> Blocks.SAND.getDefaultState();
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart gravel()
	{
		return (data, x, z, rainfall, temp) -> Blocks.GRAVEL.getDefaultState();
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart clay()
	{
		return (data, x, z, rainfall, temp) -> Blocks.CLAY.getDefaultState();
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	static ISurfacePart rock()
	{
		return (data, x, y, rainfall, temp) -> IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material)
				.getDefaultState();
	}

	BlockState get(SurfaceData data, int x, int z, float rainfall, float temp);

}
