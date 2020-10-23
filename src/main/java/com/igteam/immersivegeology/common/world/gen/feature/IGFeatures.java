package com.igteam.immersivegeology.common.world.gen.feature;

import com.igteam.immersivegeology.common.world.gen.feature.structure.ImmersiveFortressStructure;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.FortressStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class IGFeatures<FC extends IFeatureConfig> extends ForgeRegistryEntry<Feature<?>> {

    public static final Feature<NoFeatureConfig> MOSS_LAYER = new MossFeature(NoFeatureConfig::deserialize);
    public static final Feature<NoFeatureConfig> CAVE_FEATURES = new CaveFeature(NoFeatureConfig::deserialize);
    public static final Feature<NoFeatureConfig> NETHER_CAVE_FEATURES = new NetherCaveFeature(NoFeatureConfig::deserialize);

    public static final Structure<NoFeatureConfig> IMMERSIVE_NETHER_BRIDGE = (Structure)register("immersive_nether_bridge", new ImmersiveFortressStructure(NoFeatureConfig::deserialize));

    private static <C extends IFeatureConfig, F extends Feature<C>> Feature register(String id, F feature) {
        return (Feature) Registry.register(Registry.FEATURE, id, feature);
    }

}
