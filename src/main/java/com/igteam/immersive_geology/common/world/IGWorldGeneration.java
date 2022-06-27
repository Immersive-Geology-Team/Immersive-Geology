package com.igteam.immersive_geology.common.world;

import blusunrize.immersiveengineering.common.world.IECountPlacement;
import blusunrize.immersiveengineering.common.world.IEOreFeature;
import blusunrize.immersiveengineering.common.world.IEWorldGen;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.*;

public class IGWorldGeneration {
    public static Map<String, ConfiguredFeature<?, ?>> features = new HashMap<>();
    public static Set<ConfiguredFeature<?,?>> featureBlacklist = new HashSet<>();

    public static void initialize(){
        ImmersiveGeology.getNewLogger().info("Immersive Geology: Initializing World Generation");

        for (MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
            if (container.hasPattern(BlockPattern.ore)) {
                addOreGen(container, container.getName(), container.getGenerationConfig());
            } else {
                IGApi.getNewLogger().warn("Containing Material has no Ore Pattern");
            }
        }
    }

    private static void fillFeatureBlacklist() { //Probably going the way of the DODO ~Muddykat
        featureBlacklist.add(Features.ORE_GOLD);
        featureBlacklist.add(Features.ORE_IRON);
        featureBlacklist.add(Features.ORE_GOLD_EXTRA);
        featureBlacklist.add(IEWorldGen.features.get("veins")); //Remove IE Ore Generation (We'll replace it later!) Also need to look at a way to do this without Internal Classes...
        featureBlacklist.add(TinkerWorld.COPPER_ORE_FEATURE);   //We get rid of the Tinker Version of copper
        featureBlacklist.add(TinkerWorld.COBALT_ORE_FEATURE_SMALL); //And cobalt
        featureBlacklist.add(TinkerWorld.COBALT_ORE_FEATURE_LARGE);
    }

    static Map<String, IGOreConfig> configMap = new HashMap<>();

    public static void addOreGen(MaterialInterface<?> oreType, String name, IGOreConfig config)
    {
        ConfiguredFeature<?, ?> feature = new IGOreFeature(OreFeatureConfig.CODEC, config.spawnChance.get()).withConfiguration(
                new IGOreFeatureConfig(
                        oreType.getDimension(),
                        oreType,
                        config.veinSizeMin.get(), config.veinSizeMax.get())).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY.get(), 0, config.maxY.get()))
                .square()).count(config.veinsPerChunk.get());
        features.put(name, feature);

        configMap.put(feature.toString(), config);
    }

    public static boolean replaceOres = false; //disabled for now

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();

        if(replaceOres) {
            //We still allow Coal, Diamonds and Emeralds to spawn.
            if (featureBlacklist.isEmpty()) {
                IGApi.getNewLogger().warn("Blacklisting Metallic Ores from spawning. This includes ores from IE and TC - This is configurable this is a default IG process however");
                fillFeatureBlacklist();
            }

            //TODO Make config option to remove ores
            //Most Overworld Ores use this
            generation.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).removeIf((suppliedFeature) -> {
                ResourceLocation loc = suppliedFeature.get().feature.getRegistryName();
                return featureBlacklist.stream().anyMatch((blacklist) -> (Objects.equals(blacklist.feature.getRegistryName(), loc)));
            });

            //Most Nether Ores use this
            generation.getFeatures(GenerationStage.Decoration.UNDERGROUND_DECORATION).removeIf((suppliedFeature) -> {
                ResourceLocation loc = suppliedFeature.get().feature.getRegistryName();
                return featureBlacklist.stream().anyMatch((blacklist) -> (Objects.equals(blacklist.feature.getRegistryName(), loc)));
            });

            DefaultBiomeFeatures.withCommonOverworldBlocks(generation); // re-add non-ore blocks using same stage of underground_ores
            DefaultBiomeFeatures.withInfestedStone(generation);
            DefaultBiomeFeatures.withDisks(generation);
            DefaultBiomeFeatures.withClayDisks(generation);
        }

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
