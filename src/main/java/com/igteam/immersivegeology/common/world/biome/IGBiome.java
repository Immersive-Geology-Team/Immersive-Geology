package com.igteam.immersivegeology.common.world.biome;

import com.igteam.immersivegeology.common.world.biome.biomes.OceanBiome;
import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import javax.annotation.Nonnull;

public abstract class IGBiome extends Biome
{

	protected final float btemp;
	protected final float brain;

	@Override
	public float getTemperature(BlockPos pos)
	{
		int sea_level = ChunkGeneratorImmersiveOverworld.SEA_LEVEL;
		if(pos.getY() > sea_level)
		{
			float f = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX()/8.0F,
					(float)pos.getZ()/8.0F)*4.0D);
			return btemp-(f+(float)pos.getY()-sea_level)*0.05F/30.0F;
		}
		else
		{
			return btemp;
		}
	}

	public float getBrain()
	{
		return brain;
	}

	// TODO may want to add salt and fresh water distinction
	protected IGBiome(Builder builder, int waterColor, int waterFogColor, RainType type, float temperature,
					  float downfall)
	{
		super(builder.depth(0).scale(0).waterColor(waterColor).waterFogColor(waterFogColor).precipitation(type)
				.temperature(temperature).downfall(downfall)
				// Unused for now, may be used by variant biomes
				.parent(null)
				// Unused as we do a much more complex surface builder
				.surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)));
		btemp = temperature;
		brain = downfall;
	}

	protected IGBiome(Builder builder, float temperature, float downfall)
	{
		super(builder.depth(0).scale(0).waterColor(0x456CBE).waterFogColor(0x3A5B82).precipitation(RainType.RAIN)
				.temperature(0.5f).downfall(0.5f)
				// Unused for now, may be used by variant biomes
				.parent(null)
				// Unused as we do a much more complex surface builder
				.surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)));
		btemp = temperature;
		brain = downfall;
	}

	@Nonnull
	public abstract INoise2D createNoiseLayer(long seed);

	@Nonnull
	public ISurfaceBuilder getIGSurfaceBuilder()
	{
		return ISurfaceBuilder.DEFAULT;
	}

	@Nonnull
	public abstract BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain);

}
