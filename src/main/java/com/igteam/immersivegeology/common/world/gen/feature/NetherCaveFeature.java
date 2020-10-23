package com.igteam.immersivegeology.common.world.gen.feature;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGCaveBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.property.SpikePart;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;
import java.util.function.Function;

public class NetherCaveFeature extends CaveFeature {

    public NetherCaveFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49878_1_) {
        super(p_i49878_1_);
    }

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand, Material mat) {
        if (pos.getY() < 85)
        {
            return;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        int height = 41 + rand.nextInt(23);
        int radius = 8 + rand.nextInt(6);
        int maxHeightReached = 0;
        for (int y = -3; y <= height; y++)
        {
            float radiusSquared = radius * (1 - 1.5f * Math.abs(y) / height);
            if (radiusSquared < 0)
            {
                continue;
            }

            if(y <= 0){
                for (int x = -radius; x <= radius; x++)
                {
                    for (int z = -radius; z <= radius; z++) {
                        checkPos.setPos(pos).move(x, y * direction.getYOffset(), z).move(direction);
                        if (worldIn.getBlockState(checkPos).getMaterial() == net.minecraft.block.material.Material.AIR && worldIn.getBlockState(checkPos).getMaterial() == net.minecraft.block.material.Material.LAVA) {
                            return;
                        }
                    }
                }
            }

            radiusSquared *= radiusSquared;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.setPos(pos).move(x, y * direction.getYOffset(), z);
                    float actualRadius = ((x * x) + (z * z)) / radiusSquared;
                    if (actualRadius < 0.7)
                    {
                        // Fill in actual blocks
                        replaceBlock(worldIn, mutablePos, raw);
                        if (x == 0 && z == 0)
                        {
                            maxHeightReached = y;
                        }
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (worldIn.getBlockState(mutablePos.add(0, -direction.getYOffset(), 0)) == raw)
                        {
                            replaceBlock(worldIn, mutablePos, raw);
                        }
                    }
                    else if (actualRadius < 1 && rand.nextInt(3) == 0 && y > 0)
                    {
                       // placeSmallStalactite(worldIn, mutablePos, spike, raw, direction, rand, mat);
                    }
                }
            }
        }
        mutablePos.setPos(pos).move(direction, maxHeightReached - 1);
        placeSmallStalactite(worldIn, mutablePos, spike, raw, direction, rand, 1.0f, mat);
    }
}
