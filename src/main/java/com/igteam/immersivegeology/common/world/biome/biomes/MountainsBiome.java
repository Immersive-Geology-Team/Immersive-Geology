package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.BiomeHelper;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.MountainType;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.chunk.IChunk;

import javax.annotation.Nonnull;

import java.util.Random;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class MountainsBiome extends IGBiome
{
	private final float baseHeight;
	private final float scaleHeight;
	public final MountainType mountainType;
	
	public MountainsBiome(float baseHeight, float scaleHeight, MountainType mountainType)
	{
		super(new Builder().category(Category.EXTREME_HILLS).precipitation(BiomeHelper.getMountainRain(mountainType)).downfall(0.6f).temperature(0.35f), 0.35f, (mountainType == MountainType.FLOODED)?0.85f: 0.6f);

		this.baseHeight = baseHeight;
		this.scaleHeight = scaleHeight;
		
		this.mountainType = mountainType;
		
		switch(mountainType) {
			case LUSH:
				IGDefaultBiomeFeatures.addLeafShurbs(this);
			break;
			case FROZEN:
				DefaultBiomeFeatures.addIcebergs(this);
				DefaultBiomeFeatures.addBlueIce(this);
				DefaultBiomeFeatures.addFreezeTopLayer(this);
			break;
			default:
			break;
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
		switch(mountainType)
		{
		case FROZEN:
			switch(part) {
			case grass:
				return Blocks.PACKED_ICE.getDefaultState();
			case dirt:
				return Blocks.BLUE_ICE.getDefaultState();
			default:
				return Blocks.ICE.getDefaultState();
			}
		case DESERT:
			switch(part)
			{
				case grass:
					return Blocks.SAND.getDefaultState();
				case dirt:
					return Blocks.SAND.getDefaultState();
				default:
					return Blocks.SANDSTONE.getDefaultState();
			}

		case LUSH:
			switch(part)
			{
				case grass:
					return Blocks.GRASS_BLOCK.getDefaultState();
				default:
					return Blocks.DIRT.getDefaultState();
			}
		default:
			switch(part)
			{
				case grass:
					return IGRegistryGrabber.grabBlock(MaterialUseType.GRAVEL, EnumMaterials.Rhyolite.material).getDefaultState();
				default:
					return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material).getDefaultState();
			}
		}
	}
}
