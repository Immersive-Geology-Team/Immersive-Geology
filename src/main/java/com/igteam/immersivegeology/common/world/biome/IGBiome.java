package com.igteam.immersivegeology.common.world.biome;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.noise.INoise2D;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public abstract class IGBiome extends Biome {

	//TODO may want to add salt and fresh water distinction
	
	protected IGBiome(Builder builder) {
		 super(builder
		            .depth(0).scale(0).waterColor(4159204).waterFogColor(329011).precipitation(RainType.RAIN).temperature(1.0f).downfall(1.0f)
		            // Unused for now, may be used by variant biomes
		            .parent(null)
		            // Unused as we do a much more complex surface builder
		            .surfaceBuilder(new ConfiguredSurfaceBuilder<>(SurfaceBuilder.NOPE, SurfaceBuilder.AIR_CONFIG)));
		 
	}
	
	@Nonnull
    public abstract INoise2D createNoiseLayer(long seed);

    @Nonnull
    public ISurfaceBuilder getIGSurfaceBuilder()
    {
        return ISurfaceBuilder.DEFAULT;
    }
}
