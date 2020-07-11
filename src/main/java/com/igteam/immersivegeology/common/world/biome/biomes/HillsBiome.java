package com.igteam.immersivegeology.common.world.biome.biomes;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class HillsBiome extends IGBiome
{
    private final float height;

    public HillsBiome(float height)
    {
        super(new Builder().category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.6f).temperature(0.5f),0.5f,.6f);
        this.height = height;

        IGDefaultBiomeFeatures.addCarvers(this);
        IGDefaultBiomeFeatures.addTrees(this);
        
        DefaultBiomeFeatures.addStructures(this);
        DefaultBiomeFeatures.addTaigaRocks(this);
        DefaultBiomeFeatures.addBerryBushes(this);
    } 
 
    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL - 5, SEA_LEVEL + height);
    }
    
	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain) {
		// TODO Auto-generated method stub
		switch(part) {
			case grass:
				return Blocks.GRASS_BLOCK.getDefaultState();
			case dirt:
				return Blocks.DIRT.getDefaultState();
			default:
				return Blocks.DIRT.getDefaultState();
		}
	}
}
