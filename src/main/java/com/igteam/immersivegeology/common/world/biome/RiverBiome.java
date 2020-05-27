package com.igteam.immersivegeology.common.world.biome;
import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

public class RiverBiome extends IGBiome
{
    public RiverBiome()
    {
        super(new Builder().category(Category.RIVER));
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(SEA_LEVEL - 6, SEA_LEVEL - 1);
    }
}