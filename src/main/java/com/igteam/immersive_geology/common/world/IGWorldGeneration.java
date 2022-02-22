package com.igteam.immersive_geology.common.world;

import igteam.immersive_geology.config.IGOreConfig;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.APIMaterials;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.Block;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class IGWorldGeneration {

    public static Map<String, ConfiguredFeature<?, ?>> features = new HashMap<>();

    public static void initialize(){
        for(StoneEnum stones : APIMaterials.stoneValues()) {
            for (MaterialInterface container : APIMaterials.all()) {
                if (container.hasPattern(BlockPattern.ore)) {
                    Block block = container.getBlock(BlockPattern.ore);
                    if(block != null)
                    addOreGen(block, container.getName(), container.getGenerationConfig());
                }
                if (container.hasPattern(BlockPattern.geode)) {
                    Block block = container.getBlock(BlockPattern.geode);
                    if(block != null)
                        addOreGen(block, container.getName(), container.getGenerationConfig());
                }
            }
        }
    }

    public static void addOreGen(Block block, String name, IGOreConfig config)
    {
        ConfiguredFeature feature = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, block.getDefaultState(), config.veinSize.get())).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY.get(), 0, config.maxY.get()))
            .square()).count(config.veinsPerChunk.get());
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
