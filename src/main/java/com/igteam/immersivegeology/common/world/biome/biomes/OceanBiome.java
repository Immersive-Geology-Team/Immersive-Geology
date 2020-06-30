package com.igteam.immersivegeology.common.world.biome.biomes;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

public class OceanBiome extends IGBiome {
    private final float depthMin, depthMax;
    
    public OceanBiome(boolean isDeep)
    {
        super(new Builder().category(Category.OCEAN), 0x4E7280, isDeep ? 0x324E72 : 0x04770A5, RainType.RAIN, isDeep ? 10f : 15f, 2f);

        if (isDeep)
        {
            this.depthMin = SEA_LEVEL - 80;
            this.depthMax = SEA_LEVEL - 75;
            
        }
        else
        {
            this.depthMin = SEA_LEVEL - 35;
            this.depthMax = SEA_LEVEL - 25;
        }
        
        DefaultBiomeFeatures.addKelp(this);
        IGDefaultBiomeFeatures.addOceanCarvers(this);
    }

    @Override
    public ISurfaceBuilder getIGSurfaceBuilder() {
    	// TODO Auto-generated method stub
    	return ISurfaceBuilder.OCEAN;
    }
    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Uses domain warping to achieve a swirly hills effect
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, depthMin, depthMax);
    }
    
	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain) {
		// TODO Auto-generated method stub
		return Blocks.GRAVEL.getDefaultState();
	}
}