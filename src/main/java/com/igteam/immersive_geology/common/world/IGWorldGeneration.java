package com.igteam.immersive_geology.common.world;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.core.config.IGOreConfig;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;

import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import java.util.HashMap;
import java.util.Map;

public class IGWorldGeneration {

    public static Map<String, ConfiguredFeature<?, ?>> features = new HashMap<>();

    public static void initialize(){
        DeferredWorkQueue.runLater(
            () -> {
                for(MaterialEnum stoneContainer : MaterialEnum.stoneValues()) {
                    for (MaterialEnum container : MaterialEnum.values()) {
                        if (container.getMaterial().hasSubtype(MaterialUseType.ORE_STONE)) {
                            addOreGen(IGRegistrationHolder.getBlockByMaterial(stoneContainer.getMaterial(), container.getMaterial(), MaterialUseType.ORE_STONE), container.getMaterial().getName(), container.getMaterial().getGenerationConfig());
                        }
                        if(container.getMaterial().hasSubtype(MaterialUseType.GEODE)){
                            addOreGen(IGRegistrationHolder.getBlockByMaterial(MaterialUseType.GEODE, container.getMaterial()), container.getMaterial().getName(), container.getMaterial().getGenerationConfig());
                        }
                    }
                }
            }
        );

    }

    public static void addOreGen(Block block, String name, IGOreConfig config)
    {
        ConfiguredFeature feature = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, block.getDefaultState(), config.veinSize)).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY, 0, config.maxY))
            .square()).count(config.veinsPerChunk);
        features.put(name, feature);
    }

    @SubscribeEvent
    public void onBiomeLoad(BiomeLoadingEvent ev)
    {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();
        for(Map.Entry<String, ConfiguredFeature<?, ?>> e : features.entrySet())
            generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, e.getValue());
    }
}
