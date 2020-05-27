package com.igteam.immersivegeology.common.world.biome;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.block.BlockState;

public class OceanBiome extends IGBiome {
    private final float depthMin, depthMax;
    
    public OceanBiome(boolean isDeep)
    {
        super(new Builder().category(Category.OCEAN));

        if (isDeep)
        {
            this.depthMin = SEA_LEVEL - 36;
            this.depthMax = SEA_LEVEL - 10;
        }
        else
        {
            this.depthMin = SEA_LEVEL - 24;
            this.depthMax = SEA_LEVEL - 6;
        }

        //TFCDefaultBiomeFeatures.addOceanCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Uses domain warping to achieve a swirly hills effect
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, depthMin, depthMax);
    }
}