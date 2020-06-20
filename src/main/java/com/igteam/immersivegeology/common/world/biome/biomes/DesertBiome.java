package com.igteam.immersivegeology.common.world.biome.biomes;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.world.biome.Biome.RainType;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class DesertBiome extends IGBiome
{
    private final float minHeight;
    private final float maxHeight;

    public DesertBiome(float minHeight, float maxHeight)
    {
        super(new Builder().category(Category.DESERT).precipitation(RainType.NONE).downfall(0f).temperature(0.95f),0.95f,0f);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        
        IGDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
    	return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL + minHeight, SEA_LEVEL + maxHeight);
    }
}