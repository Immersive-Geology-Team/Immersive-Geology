package com.igteam.immersive_geology.common.world.feature;

import com.mojang.serialization.Codec;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;

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
                        return this.func_207803_a(reader, rand, config, d0, d1, d2, d3, d4, d5, k, l, i1, j1, k1);
                    }
                }
            }
        }

        return false;
    }

    public static final class IGFillerBlockType {
        public static final RuleTest GRANITE_OVERWORLD = new TagMatchRuleTest(StoneEnum.Granite.getBlockTag(BlockPattern.block));
    }
}
