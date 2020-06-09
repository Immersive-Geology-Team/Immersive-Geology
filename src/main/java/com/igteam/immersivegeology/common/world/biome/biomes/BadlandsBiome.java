package com.igteam.immersivegeology.common.world.biome.biomes;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class BadlandsBiome extends IGBiome
{
    public BadlandsBiome()
    {
        super(new IGBiome.Builder().category(Category.MESA));

        IGDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Normal flat noise, lowered by inverted power-ridge noise, looks like badlands
        final INoise2D ridgeNoise = new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).map(x -> 1.3f * -(x > 0 ? (float) Math.pow(x, 3.2f) : 0.5f * x)).scaled(-1f, 0.3f, -1f, 1f).terraces(16).scaled(-20, 0);
        return new SimplexNoise2D(seed).octaves(6).spread(0.08f).scaled(SEA_LEVEL + 22, SEA_LEVEL + 32).add(ridgeNoise);
    }
}
