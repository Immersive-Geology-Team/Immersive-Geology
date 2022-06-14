package com.igteam.immersive_geology.common.world;

import igteam.immersive_geology.IGApi;
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
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class IGWorldGeneration {
    public static List<ConfiguredFeature<?, ?>> features = new ArrayList<ConfiguredFeature<?, ?>>();

    public static void initialize(){
        IGApi.getNewLogger().warn("Immersive Geology: Initializing World Generation");

        for(StoneEnum stone : APIMaterials.stoneValues()) {
            for (MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
                if (container.hasPattern(BlockPattern.ore)) {
                    Block block = container.getBlock(BlockPattern.ore, stone);
                    if (block != null) {
                        addOreGen(block, container.getName(), container.getGenerationConfig());
                        IGApi.getNewLogger().warn("Created New Ore Generation");
                    } else {
                        IGApi.getNewLogger().warn("Failed to find Ore from: " + container.getName() + " and " + stone.getName());
                    }
                } else {
                    IGApi.getNewLogger().warn("Containing Material has no Ore Pattern");
                }
            }
        }
    }

    public static void addOreGen(Block block, String name, IGOreConfig config)
    {
        ConfiguredFeature<?, ?> feature = Feature.ORE.withConfiguration(
                new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                        block.getDefaultState(),
                        config.veinSize.get())).range(config.maxY.get()).square().count(config.veinsPerChunk.get());

        features.add(feature);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();
        for (ConfiguredFeature<?, ?> ore : features) {
            generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
            IGApi.getNewLogger().warn("Added Ore as Feature!");
        }
    }
}
