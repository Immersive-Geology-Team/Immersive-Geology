package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class ShoreBiome extends IGBiome
{
	private final boolean isStone;

	public ShoreBiome(boolean isStone)
	{
		super(new Biome.Builder().category(Category.BEACH).precipitation(RainType.RAIN).downfall(0.68f).temperature(0.35f), 0.35f, 0.68f);
		this.isStone = isStone;
		IGDefaultBiomeFeatures.addCarvers(this);
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return (x, z) -> SEA_LEVEL;
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		if(isStone)
		{
			switch(part)
			{
				case grass:
					return IGRegistryGrabber.grabBlock(MaterialUseType.GRAVEL, EnumMaterials.Granite.material).getDefaultState();
				case dirt:
					return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material).getDefaultState();
				default:
					return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material).getDefaultState();
			}
		}
		else
		{
			switch(part)
			{
				case grass:
					return Blocks.SAND.getDefaultState();
				case dirt:
					return Blocks.SANDSTONE.getDefaultState();
				default:
					return Blocks.SANDSTONE.getDefaultState();
			}
		}
	}
}