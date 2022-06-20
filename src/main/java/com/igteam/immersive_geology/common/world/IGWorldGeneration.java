package com.igteam.immersive_geology.common.world;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.world.feature.IGOreFeature;
import com.igteam.immersive_geology.common.world.feature.IGOreFeatureConfig;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.config.IGOreConfig;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.APIMaterials;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialSourceWorld;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import net.minecraft.block.Block;
import net.minecraft.world.biome.DefaultBiomeFeatures;
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
                for(MaterialInterface<MaterialBaseStone> stonetype : StoneEnum.values()) {
                    if(stonetype.generateOreFor(container)) {
                        Block block = stonetype.getBlock(BlockPattern.ore, container);
                        if (block != null) {
                            addOreGen(container, container.getName(), container.getGenerationConfig());
                        } else {
                            IGApi.getNewLogger().warn("Failed to find Ore from: " + container.getName() + " and " + StoneEnum.Stone.getName());
                        }
                    }
                }
            } else {
                IGApi.getNewLogger().warn("Containing Material has no Ore Pattern");
            }
        }

    }

    static Map<String, IGOreConfig> configMap = new HashMap<>();

    public static void addOreGen(MaterialInterface<?> oreType, String name, IGOreConfig config)
    {
        ConfiguredFeature<?, ?> feature = new IGOreFeature(OreFeatureConfig.CODEC, config.spawnChance.get()).withConfiguration(
                new IGOreFeatureConfig(
                        oreType.getDimension().equals(MaterialSourceWorld.overworld) ? OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD : OreFeatureConfig.FillerBlockType.NETHERRACK,
                        oreType,
                        config.veinSizeMin.get(), config.veinSizeMax.get())).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY.get(), 0, config.maxY.get()))
                .square()).count(config.veinsPerChunk.get());
        features.put(name, feature);

        configMap.put(feature.toString(), config);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();

        //TODO Make config option to remove ores
        generation.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).clear();

        DefaultBiomeFeatures.withCommonOverworldBlocks(generation); // re-add non-ore blocks using same stage of underground_ores
        DefaultBiomeFeatures.withInfestedStone(generation);
        DefaultBiomeFeatures.withDisks(generation);
        DefaultBiomeFeatures.withClayDisks(generation);

        //TODO Make Config Option to Disable All IG Ores
        for (ConfiguredFeature<?, ?> ore : features.values()) {
            IGOreConfig config = configMap.get(ore.toString());
            if(config.sourceWorld.get().equals(MaterialSourceWorld.overworld)) {
                generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
            }
        }

        for (ConfiguredFeature<?, ?> ore : features.values()) {
            IGOreConfig config = configMap.get(ore.toString());
            if(config.sourceWorld.get().equals(MaterialSourceWorld.nether)) {
                generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ore);
            }
        }
    }
}
