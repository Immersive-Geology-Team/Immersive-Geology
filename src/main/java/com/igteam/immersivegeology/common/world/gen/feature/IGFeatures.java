package com.igteam.immersivegeology.common.world.gen.feature;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class IGFeatures {

    public static final Feature<NoFeatureConfig> MOSS_LAYER = new MossFeature(NoFeatureConfig::deserialize);
    public static final Feature<NoFeatureConfig> CAVE_FEATURES = new CaveFeature(NoFeatureConfig::deserialize);
    public static final Feature<NoFeatureConfig> NETHER_CAVE_FEATURES = new NetherCaveFeature(NoFeatureConfig::deserialize);
}
