package com.igteam.immersive_geology.common.world.feature;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

public class IGOreFeatureConfig extends OreFeatureConfig {

    final int minSize, maxSize;
    public IGOreFeatureConfig(RuleTest p_i241989_1_, BlockState state, int sizeMin, int sizeMax) {
        super(p_i241989_1_, state, sizeMax);
        this.minSize = sizeMin;
        this.maxSize = sizeMax;
    }
}
