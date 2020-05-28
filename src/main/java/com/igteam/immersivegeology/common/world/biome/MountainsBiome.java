package com.igteam.immersivegeology.common.world.biome;
import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.block.BlockState;

public class MountainsBiome extends IGBiome
{
    private final float baseHeight;
    private final float scaleHeight;
    private final boolean isOceanMountains;

    public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains)
    {
        super(new Builder().category(Category.EXTREME_HILLS));

        this.baseHeight = baseHeight;
        this.scaleHeight = scaleHeight;
        this.isOceanMountains = isOceanMountains;

        //TFCDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Power scaled noise, looks like mountains over large area
        final INoise2D mountainNoise = new SimplexNoise2D(seed).ridged().octaves(8).spread(0.14f).map(x -> 2.67f * (float) Math.pow(0.5f * (x + 1), 3.2f) - 0.8f);
        
        return (x, z) -> SEA_LEVEL + baseHeight + scaleHeight * mountainNoise.noise(x, z);
    }

}
