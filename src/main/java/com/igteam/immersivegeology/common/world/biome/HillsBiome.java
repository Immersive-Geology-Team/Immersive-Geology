package com.igteam.immersivegeology.common.world.biome;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class HillsBiome extends IGBiome
{
    private final float height;

    public HillsBiome(float height)
    {
        super(new Builder().category(Category.PLAINS));
        this.height = height;

      //  TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL - 5, SEA_LEVEL + height);
    }
}
