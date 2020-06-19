package com.igteam.immersivegeology.common.world.biome.biomes;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.world.biome.Biome.RainType;


public class OasisBiome extends IGBiome
{
    public OasisBiome()
    {
        super(new Builder().category(Category.RIVER).precipitation(RainType.RAIN).downfall(0.05f).temperature(0.91f),.91f,.05f);

        IGDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).scaled(SEA_LEVEL - 5, SEA_LEVEL + 2);
    }
}