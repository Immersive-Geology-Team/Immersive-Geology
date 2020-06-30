package com.igteam.immersivegeology.common.world.biome.biomes;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeFeatures;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class PlainsBiome extends IGBiome
{
    private final float minHeight;
    private final float maxHeight;

    public PlainsBiome(float minHeight, float maxHeight)
    {
        super(new Builder().category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.4f).temperature(0.65f),0.65f,0.4f);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        
        IGDefaultBiomeFeatures.addCarvers(this);
        DefaultBiomeFeatures.addGrass(this);
        DefaultBiomeFeatures.addTaigaRocks(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(SEA_LEVEL + minHeight, SEA_LEVEL + maxHeight);
    }
    
	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain) {
		// TODO Auto-generated method stub
		return Blocks.GRASS_BLOCK.getDefaultState();
	}
}