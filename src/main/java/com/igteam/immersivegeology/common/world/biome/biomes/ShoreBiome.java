package com.igteam.immersivegeology.common.world.biome.biomes;
import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class ShoreBiome extends IGBiome
{
    private final boolean isStone;

    public ShoreBiome(boolean isStone)
    {
        super(new Biome.Builder().category(Category.BEACH).precipitation(RainType.RAIN).downfall(0.68f).temperature(0.35f),0.35f,0.68f);
        this.isStone = isStone;
        IGDefaultBiomeFeatures.addCarvers(this);
    }

    @Nonnull
    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return (x, z) -> SEA_LEVEL;
    }
    
	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain) {
		// TODO Auto-generated method stub
		if(isStone) {	
			return IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material).getDefaultState();
		} else {
			return Blocks.SAND.getDefaultState();
		}
	}
}