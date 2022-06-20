package com.igteam.immersive_geology.common.world.feature;

import com.igteam.immersive_geology.common.world.helper.SimplexNoise;
import com.mojang.serialization.Codec;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.apache.logging.log4j.Logger;

import java.util.BitSet;
import java.util.Random;

public class IGOreFeature extends OreFeature {

    private final int spawnChance;
    public IGOreFeature(Codec<OreFeatureConfig> codec, int spawnChance) {
        super(codec);
        this.spawnChance = spawnChance;
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, OreFeatureConfig config) {
        float f = rand.nextFloat() * (float)Math.PI;
        float f1 = (float)config.size / 8.0F;
        int i = MathHelper.ceil(((float)config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d0 = (double)pos.getX() + Math.sin((double)f) * (double)f1;
        double d1 = (double)pos.getX() - Math.sin((double)f) * (double)f1;
        double d2 = (double)pos.getZ() + Math.cos((double)f) * (double)f1;
        double d3 = (double)pos.getZ() - Math.cos((double)f) * (double)f1;
        int j = 2;
        double d4 = (double)(pos.getY() + rand.nextInt(3) - 2);
        double d5 = (double)(pos.getY() + rand.nextInt(3) - 2);
        int k = pos.getX() - MathHelper.ceil(f1) - i;
        int l = pos.getY() - 2 - i;
        int i1 = pos.getZ() - MathHelper.ceil(f1) - i;
        int j1 = 2 * (MathHelper.ceil(f1) + i);
        int k1 = 2 * (2 + i);

        if(rand.nextInt(10000) < spawnChance) {
            for (int l1 = k; l1 <= k + j1; ++l1) {
                for (int i2 = i1; i2 <= i1 + j1; ++i2) {
                    if (l <= reader.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, l1, i2)) {
                        return generateVein(reader, rand, (IGOreFeatureConfig) config, d0, d1, d2, d3, d4, d5, k, l, i1, j1, k1);
                    }
                }
            }
        }

        return false;
    }

    protected boolean generateVein(IWorld worldIn, Random random, IGOreFeatureConfig config, double p_207803_4_, double p_207803_6_, double p_207803_8_, double p_207803_10_, double p_207803_12_, double p_207803_14_, int p_207803_16_, int p_207803_17_, int p_207803_18_, int p_207803_19_, int p_207803_20_) {
        //This Function generates the actual ore vein pocket. It's pretty much a modified version of the vanilla ore generation code.
        //We add Simplex Noise to help break up the elliptical nature of the ore veins these provide

        int i = 0;
        BitSet bitset = new BitSet(p_207803_19_ * p_207803_20_ * p_207803_19_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int j = (int) Math.floor(Math.random()*(config.maxSize-config.minSize+1)+config.minSize);
        double[] adouble = new double[j * 4];

        BlockState granteOre = StoneEnum.Granite.getBlock(BlockPattern.ore, config.oreType).getDefaultState();
        BlockState stoneOre = StoneEnum.Stone.getBlock(BlockPattern.ore, config.oreType).getDefaultState();
        BlockState netherOre = StoneEnum.Netherrack.getBlock(BlockPattern.ore, config.oreType).getDefaultState();

        for(int k = 0; k < j; ++k) {
            float f = (float)k / (float)j;
            double d0 = MathHelper.lerp((double)f, p_207803_4_, p_207803_6_);
            double d2 = MathHelper.lerp((double)f, p_207803_12_, p_207803_14_);
            double d4 = MathHelper.lerp((double)f, p_207803_8_, p_207803_10_);
            double d6 = random.nextDouble() * (double)j / 16.0D;
            double d7 = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0F) * d6 + 1.0D) / 2.0D;
            adouble[k * 4 + 0] = d0;
            adouble[k * 4 + 1] = d2;
            adouble[k * 4 + 2] = d4;
            adouble[k * 4 + 3] = d7;
        }

        for(int i3 = 0; i3 < j - 1; ++i3) {
            if (!(adouble[i3 * 4 + 3] <= 0.0D)) {
                for(int k3 = i3 + 1; k3 < j; ++k3) {
                    if (!(adouble[k3 * 4 + 3] <= 0.0D)) {
                        double d12 = adouble[i3 * 4 + 0] - adouble[k3 * 4 + 0];
                        double d13 = adouble[i3 * 4 + 1] - adouble[k3 * 4 + 1];
                        double d14 = adouble[i3 * 4 + 2] - adouble[k3 * 4 + 2];
                        double d15 = adouble[i3 * 4 + 3] - adouble[k3 * 4 + 3];
                        if (d15 * d15 > d12 * d12 + d13 * d13 + d14 * d14) {
                            if (d15 > 0.0D) {
                                adouble[k3 * 4 + 3] = -1.0D;
                            } else {
                                adouble[i3 * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for(int j3 = 0; j3 < j; ++j3) {
            double d11 = adouble[j3 * 4 + 3];
            if (!(d11 < 0.0D)) {
                double d1 = adouble[j3 * 4 + 0];
                double d3 = adouble[j3 * 4 + 1];
                double d5 = adouble[j3 * 4 + 2];
                int l = Math.max(MathHelper.floor(d1 - d11), p_207803_16_);
                int l3 = Math.max(MathHelper.floor(d3 - d11), p_207803_17_);
                int i1 = Math.max(MathHelper.floor(d5 - d11), p_207803_18_);
                int j1 = Math.max(MathHelper.floor(d1 + d11), l);
                int k1 = Math.max(MathHelper.floor(d3 + d11), l3);
                int l1 = Math.max(MathHelper.floor(d5 + d11), i1);

                for(int i2 = l; i2 <= j1; ++i2) {
                    double d8 = ((double)i2 + 0.5D - d1) / d11;
                    if (d8 * d8 < 1.0D) {
                        for(int j2 = l3; j2 <= k1; ++j2) {
                            double d9 = ((double)j2 + 0.5D - d3) / d11;
                            if (d8 * d8 + d9 * d9 < 1.0D) {
                                for(int k2 = i1; k2 <= l1; ++k2) {
                                    double d10 = ((double)k2 + 0.5D - d5) / d11;
                                    if (d8 * d8 + d9 * d9 + d10 * d10 < 1.0D) {
                                        int l2 = i2 - p_207803_16_ + (j2 - p_207803_17_) * p_207803_19_ + (k2 - p_207803_18_) * p_207803_19_ * p_207803_20_;
                                        if (!bitset.get(l2)) {
                                            bitset.set(l2);
                                            blockpos$mutable.setPos(i2, j2, k2);
                                            double noiseValCheck = SimplexNoise.noise(i2, j2, k2);
                                            if(noiseValCheck > -0.5){
                                                BlockState worldState = worldIn.getBlockState(blockpos$mutable);
                                                if (config.target.test(worldState, random)) {
                                                    if(worldState.getBlock().equals(Blocks.NETHERRACK)){ //TODO Not a very 'nice' way of doing this, but it works well enough.
                                                        worldIn.setBlockState(blockpos$mutable, netherOre, 2);
                                                    } else if (worldState.getBlock().equals(Blocks.GRANITE)) {
                                                        worldIn.setBlockState(blockpos$mutable, granteOre, 2);
                                                    } else {
                                                        worldIn.setBlockState(blockpos$mutable, stoneOre, 2);
                                                    }
                                                }
                                            }
                                            ++i;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return i > 0;
    }
}
