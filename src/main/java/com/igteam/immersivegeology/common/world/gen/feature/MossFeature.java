package com.igteam.immersivegeology.common.world.gen.feature;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGLayerBase;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.ForestBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.ForestType;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import java.util.Random;
import java.util.function.Function;

public class MossFeature extends Feature<NoFeatureConfig> {

    public MossFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> deserializer)
    {
        super(deserializer);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockPos.MutableBlockPos block = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos block_above = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos block_under = new BlockPos.MutableBlockPos();

        for (int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
            for (int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
                int lvt_10_1_ = pos.getX() + lvt_8_1_;
                int lvt_11_1_ = pos.getZ() + lvt_9_1_;
                int lvt_12_1_ = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_10_1_, lvt_11_1_);
                block.setPos(lvt_10_1_, lvt_12_1_, lvt_11_1_);
                block_above.setPos(block).move(Direction.UP, 1);
                block_under.setPos(block).move(Direction.DOWN, 1);

                BlockState lvt_14_1_ = worldIn.getBlockState(block_above);
                BlockState state_under = worldIn.getBlockState(block_under);
                Biome biome = worldIn.getBiome(block);

                if(biome instanceof ForestBiome) {
                    ForestBiome forest = (ForestBiome) biome;
                    if(forest.getType() == ForestType.SWEDISH) {
                        if (lvt_14_1_.getMaterial() == Material.AIR && state_under.getBlock() == Blocks.DIRT || state_under.getBlock() == Blocks.GRASS_BLOCK || state_under.getBlock() == Blocks.PODZOL) {
                            if (rand.nextInt(8) != 0)
                                worldIn.setBlockState(block, IGRegistryGrabber.grabBlock(MaterialUseType.LAYER, EnumMaterials.Moss.material).getDefaultState().with(IGLayerBase.LAYERS, 2 + rand.nextInt(4)), 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}
