package com.igteam.immersive_geology.common.world.feature;

import igteam.immersive_geology.materials.helper.MaterialInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

public class IGOreFeatureConfig extends OreFeatureConfig {

    final int minSize, maxSize;

    final MaterialInterface<?> oreType;
    public IGOreFeatureConfig(RuleTest p_i241989_1_, MaterialInterface<?> oreType, int sizeMin, int sizeMax) {
        super(p_i241989_1_, Blocks.STONE.getDefaultState(), sizeMax);
        this.minSize = sizeMin;
        this.maxSize = sizeMax;
        this.oreType = oreType;
    }
}
