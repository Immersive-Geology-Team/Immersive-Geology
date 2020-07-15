package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeFeatures;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class MountainsBiome extends IGBiome
{
	private final float baseHeight;
	private final float scaleHeight;
	private final boolean isOceanMountains;
	private final boolean isLushMountains;


	public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains)
	{
		this(baseHeight, scaleHeight, isOceanMountains, false);
	}

	public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains, boolean isLushMountains)
	{
		super(new Builder().category(Category.EXTREME_HILLS).precipitation(RainType.RAIN).downfall(0.6f).temperature(0.35f), 0.35f, (isOceanMountains)?0.85f: 0.6f);

		this.baseHeight = baseHeight;
		this.scaleHeight = scaleHeight;
		this.isOceanMountains = isOceanMountains;
		this.isLushMountains = isLushMountains;

		if(isLushMountains)
		{
			IGDefaultBiomeFeatures.addLeafShurbs(this);
		}

		IGDefaultBiomeFeatures.addCarvers(this);
		DefaultBiomeFeatures.addTaigaRocks(this);

	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		// Power scaled noise, looks like mountains over large area
		final INoise2D mountainNoise = new SimplexNoise2D(seed).ridged().octaves(8).spread(0.14f).map(x -> 2.67f*(float)Math.pow(0.5f*(x+1), 3.2f)-0.8f);

		return (x, z) -> SEA_LEVEL+baseHeight+scaleHeight*mountainNoise.noise(x, z);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		// TODO Auto-generated method stub
		if(isLushMountains)
		{
			switch(part)
			{
				case grass:
					return Blocks.GRASS_BLOCK.getDefaultState();
				case dirt:
					return Blocks.DIRT.getDefaultState();
				default:
					return Blocks.DIRT.getDefaultState();
			}
		}
		else
		{
			switch(part)
			{
				case grass:
					return IGRegistryGrabber.grabBlock(MaterialUseType.GRAVEL, EnumMaterials.Rhyolite.material).getDefaultState();
				case dirt:
					return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material).getDefaultState();
				default:
					return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material).getDefaultState();
			}


		}
	}

}
