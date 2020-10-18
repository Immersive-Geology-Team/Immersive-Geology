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
import org.lwjgl.system.CallbackI;

import java.util.Random;
import java.util.function.Function;

public class CaveFeature extends Feature<NoFeatureConfig> {

    public CaveFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49878_1_) {
        super(p_i49878_1_);
    }

    @Override
    public boolean place(IWorld iWorld, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig noFeatureConfig) {
        BlockState stateAt = iWorld.getBlockState(pos);
        if(stateAt.getBlock() == Blocks.CAVE_AIR || (iWorld.getDimension().getType() == DimensionType.THE_NETHER && (stateAt.getBlock() == Blocks.CAVE_AIR || stateAt.getBlock() == Blocks.AIR))){
            Direction dir = rand.nextBoolean() ? Direction.UP : Direction.DOWN;
            BlockState wall = iWorld.getBlockState(pos.offset(dir.getOpposite()));
            if(wall.getBlock() instanceof IGMaterialBlock){
                IGMaterialBlock igBlock = (IGMaterialBlock) wall.getBlock();
                if(igBlock.getUseType() == MaterialUseType.ROCK){
                    place(iWorld, pos, wall, wall, dir, rand, igBlock.getMaterial());
                } else {
                    dir = dir.getOpposite();
                    wall = iWorld.getBlockState(pos.offset(dir.getOpposite()));
                    if(wall.getBlock() instanceof IGMaterialBlock) {
                        igBlock = (IGMaterialBlock) wall.getBlock();
                        if (igBlock.getUseType() == MaterialUseType.ROCK) {
                            place(iWorld, pos, wall, wall, dir, rand, igBlock.getMaterial());
                        }
                    }
                }
            } else {
                dir = dir.getOpposite();
                wall = iWorld.getBlockState(pos.offset(dir.getOpposite()));
                if(wall.getBlock() instanceof IGMaterialBlock) {
                    IGMaterialBlock igBlock = (IGMaterialBlock) wall.getBlock();
                    if (igBlock.getUseType() == MaterialUseType.ROCK) {
                        place(iWorld, pos, wall, wall, dir, rand, igBlock.getMaterial());
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand, Material mat){
        placeSmallStalactite(worldIn, pos, spike, raw, direction, rand, mat);
    }
    protected void placeSmallStalactite(IWorld worldIn, BlockPos pos, BlockState rock, BlockState raw, Direction dir, Random rand, Material mat){
        placeSmallStalactite(worldIn, pos, rock, raw, dir, rand, rand.nextFloat(), mat);
    }

    protected void placeSmallStalactite(IWorld worldIn, BlockPos pos, BlockState rock, BlockState raw, Direction dir, Random rand, float size, Material mat){
        if(size < 0.2f){
            replaceBlock(worldIn, pos, IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.top).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 1), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.tip).with(IGCaveBlock.FACING, dir));
        } else if(size < 0.7f){
            replaceBlock(worldIn, pos, IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.middle).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 1), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.top).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 2), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.tip).with(IGCaveBlock.FACING, dir));
        } else {
            replaceBlock(worldIn, pos, IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.base).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 1), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.middle).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 2), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.top).with(IGCaveBlock.FACING, dir));
            replaceBlock(worldIn, pos.offset(dir, 3), IGRegistryGrabber.grabBlock(MaterialUseType.SPIKE, mat).getDefaultState().with(IGCaveBlock.PART, SpikePart.tip).with(IGCaveBlock.FACING, dir));
        }
    }

    protected void replaceBlock(IWorld world, BlockPos pos, BlockState state)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR || block == Blocks.AIR)
        {
            setBlockState(world, pos, state);
        }
    }
}
