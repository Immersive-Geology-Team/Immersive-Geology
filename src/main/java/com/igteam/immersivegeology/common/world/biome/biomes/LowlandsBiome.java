package com.igteam.immersivegeology.common.world.biome.biomes;
import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor;

public class LowlandsBiome extends IGBiome
{
    public LowlandsBiome()
    {
        super(new Builder().category(Category.PLAINS));
        IGDefaultBiomeFeatures.addCarvers(this);
    }
    
    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.55f).scaled(SEA_LEVEL - 6, SEA_LEVEL + 7).flattened(SEA_LEVEL - 4, SEA_LEVEL + 3);
    }
}