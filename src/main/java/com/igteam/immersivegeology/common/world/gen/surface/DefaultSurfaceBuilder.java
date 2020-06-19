package com.igteam.immersivegeology.common.world.gen.surface;

import java.util.Random;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveSurfaceBuilderConfig;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunk;

public class DefaultSurfaceBuilder implements ISurfaceBuilder {
	    private final ImmersiveSurfaceBuilderConfig config;
	    private final int soilLayers;
	    public static final int SEA_LEVEL = 96;
	    
	    public DefaultSurfaceBuilder(ImmersiveSurfaceBuilderConfig config, int soilLayers)
	    {
	        this.config = config;
	        this.soilLayers = soilLayers; 
	    }

	    @Override
	    public void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, float temperature, float rainfall, float noise)
	    {
	        int surfaceFlag = -1;
	        int localX = x & 15;
	        int localZ = z & 15;
	        
	        Block defaultBlock = IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material);
	        
	        SurfaceData surface = new SurfaceData(chunkIn);
	        
	        BlockState stateUnder = config.getUnder().get(surface, localX, localZ);
	        BlockState stateUnderWater = config.getUnderWater().get(surface, localX, localZ);
	        BlockState topBlock = config.getTop().get(surface, localX, localZ);
	        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	        for (int y = startHeight; y >= 0 && surfaceFlag != 0; y--)
	        {
	            pos.setPos(localX, y, localZ);
	            BlockState stateAt = chunkIn.getBlockState(pos);
	            if (stateAt.isAir(chunkIn, pos))
	            {
	                // Air, so continue downwards and wait for surface to appear
	                surfaceFlag = -1;
	            }
	            else if (stateAt.getBlock() instanceof IGBaseBlock)//== defaultBlock.getBlock())
	            {
	                if (surfaceFlag == -1)
	                {
	                    if (y >= (SEA_LEVEL - 1))
	                    {
	                        // Above water, just hit surface
	                        surfaceFlag = getSoilLayers(y, random);
	                        if (surfaceFlag > 0)
	                        {
	                            chunkIn.setBlockState(pos, topBlock, false);
	                        }
	                    }
	                    else
	                    {
	                        surfaceFlag = 1;
	                        chunkIn.setBlockState(pos, stateUnderWater, false);
	                    }
	                }
	                else if (surfaceFlag > 0)
	                {
	                    surfaceFlag--;
	                    chunkIn.setBlockState(pos, stateUnder, false);
	                }
	            }
	        }
	    }

	    protected int getSoilLayers(int y, Random random)
	    {
	        int maxHeight = 140 + random.nextInt(3) - random.nextInt(3);
	        if (y > maxHeight)
	        {
	            return 0;
	        }
	        return (int) MathHelper.clamp(0.08f * (maxHeight - y) * soilLayers, 1, soilLayers);
	    }
	}
