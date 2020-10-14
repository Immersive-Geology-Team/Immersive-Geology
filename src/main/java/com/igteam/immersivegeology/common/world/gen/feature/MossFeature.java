package com.igteam.immersivegeology.common.world.gen.feature;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGLayerBase;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
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
        BlockPos.MutableBlockPos lvt_6_1_ = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos lvt_7_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
            for (int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
                int lvt_10_1_ = pos.getX() + lvt_8_1_;
                int lvt_11_1_ = pos.getZ() + lvt_9_1_;
                int lvt_12_1_ = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_10_1_, lvt_11_1_);
                lvt_6_1_.setPos(lvt_10_1_, lvt_12_1_, lvt_11_1_);
                lvt_7_1_.setPos(lvt_6_1_).move(Direction.UP, 1);

                BlockState lvt_14_1_ = worldIn.getBlockState(lvt_7_1_);
                if (lvt_14_1_.getMaterial() == Material.AIR) {
                    if (rand.nextInt(8) != 0)
                        worldIn.setBlockState(lvt_6_1_, IGRegistryGrabber.grabBlock(MaterialUseType.LAYER, EnumMaterials.Moss.material).getDefaultState().with(IGLayerBase.LAYERS, 2 + rand.nextInt(4)), 2);
                }
            }
        }
        return true;
    }
}
