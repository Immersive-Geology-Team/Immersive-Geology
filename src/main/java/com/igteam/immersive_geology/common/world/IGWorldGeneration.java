package com.igteam.immersive_geology.common.world;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.world.feature.IGOreFeature;
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
import net.minecraft.world.gen.feature.NoFeatureConfig;
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
        ImmersiveGeology.getNewLogger().warn("Immersive Geology: Initializing World Generation");

        for (MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
            if (container.hasPattern(BlockPattern.ore)) {
                Block block = StoneEnum.Stone.getBlock(BlockPattern.ore, container);
                if (block != null) {
                    addOreGen(block, container.getName(), container.getGenerationConfig());
                } else {
                    IGApi.getNewLogger().warn("Failed to find Ore from: " + container.getName() + " and " + StoneEnum.Stone.getName());
                }
            } else {
                IGApi.getNewLogger().warn("Containing Material has no Ore Pattern");
            }
        }

    }

    public static void addOreGen(Block block, String name, IGOreConfig config)
    {
        ConfiguredFeature<?, ?> feature = new IGOreFeature(OreFeatureConfig.CODEC, config.spawnChance.get()).withConfiguration(
                new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                        block.getDefaultState(),
                        config.veinSize.get())).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY.get(), 0, config.maxY.get()))
                .square()).count(config.veinsPerChunk.get());
        features.put(name, feature);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();

        //TODO Make config option to remove ores
        generation.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).clear();

        //TODO Make Config Option to Disable All IG Ores
        for (ConfiguredFeature<?, ?> ore : features.values()) {
            generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
        }

    }
}
