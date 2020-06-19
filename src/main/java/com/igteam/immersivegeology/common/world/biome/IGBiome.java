package com.igteam.immersivegeology.common.world.biome;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.noise.INoise2D;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public abstract class IGBiome extends Biome {

	protected final float btemp;
	protected final float brain;
	
	public float getBtemp() {
		//TODO Overrride original methods for both rain and temp (other mods use this for their things, if it's not working it bad)
		//TODO implement y testing for allowing mountain tops to be cold
		return btemp;
	}
	public float getBrain() {
		return brain;
	}
	
	//TODO may want to add salt and fresh water distinction
	protected IGBiome(Builder builder, int waterColor, int waterFogColor, RainType type, float temperature, float downfall) {
		 super(builder
		            .depth(0).scale(0).waterColor(waterColor).waterFogColor(waterFogColor).precipitation(type).temperature(temperature).downfall(downfall)
		            // Unused for now, may be used by variant biomes
		            .parent(null)
		            // Unused as we do a much more complex surface builder
		            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)));
		 btemp = temperature;
		 brain = downfall;
	}
	protected IGBiome(Builder builder, float temperature, float downfall) {
		 super(builder
		            .depth(0).scale(0).waterColor(0x456CBE).waterFogColor(0x3A5B82).precipitation(RainType.RAIN).temperature(0.5f).downfall(0.5f)
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
}
