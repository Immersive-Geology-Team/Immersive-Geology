package igteam.immersive_geology.common.world;

import blusunrize.immersiveengineering.common.world.IEWorldGen;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.world.feature.IGOreFeature;
import igteam.immersive_geology.common.world.feature.IGOreFeatureConfig;
import igteam.api.IGApi;
import igteam.api.config.IGOreConfig;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.pattern.BlockPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.*;

import static igteam.immersive_geology.core.registration.IGRegistrationHolder.register;

public class IGWorldGeneration {
    public static Map<String, ConfiguredFeature<?, ?>> features = new HashMap<>();
    public static Set<ConfiguredFeature<?,?>> featureBlacklist = new HashSet<>();

    public static void initialize(){
        ImmersiveGeology.getNewLogger().info("Immersive Geology: Initializing World Generation");

        for (MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
            for (BlockPattern pattern : BlockPattern.values()) {
                if (container.generateForBlockPattern(pattern)) {
                    addOreGen(container, container.getName(), pattern, container.getGenerationConfig());
                }
            }
        }
    }

    static Map<String, IFeatureConfig> configMap = new HashMap<>();

    public static void addOreGen(MaterialInterface<?> oreType, String name, BlockPattern blockPattern, IFeatureConfig fc)
    {
        ConfiguredFeature<?, ?> feature;
        if(fc instanceof IGOreConfig) {
            IGOreConfig config = (IGOreConfig) fc;
            feature = new IGOreFeature(OreFeatureConfig.CODEC, config.spawnChance.get()).withConfiguration(
                new IGOreFeatureConfig(
                        oreType.getDimension(),
                        oreType,
                        blockPattern,
                        config.veinSizeMin.get(), config.veinSizeMax.get())).withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(config.minY.get(), 0, config.maxY.get()))
                .square()).count(config.veinsPerChunk.get());
            features.put(name, feature);

            configMap.put(feature.toString(), fc);
        }
        // TODO This method of 'switching' the ore config type is bad, need to refactor this at some point.
        // However this should be functional enough for now.
        if(fc instanceof SphereReplaceConfig) {
            IGApi.getNewLogger().info("Mapping Kaolinite Generation");
            feature = register("disk_kaolinite_clay", new SphereReplaceFeature(SphereReplaceConfig.CODEC).withConfiguration((SphereReplaceConfig) fc));
            features.put(name, feature);
            configMap.put(feature.toString(), fc);
        }
    }

    public static boolean replaceOres = false; //disabled for now

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        BiomeGenerationSettingsBuilder generation = ev.getGeneration();

        if(replaceOres) {
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
            IFeatureConfig fc = configMap.get(ore.toString());
            if(fc instanceof IGOreConfig) {
                IGOreConfig config = (IGOreConfig) fc;
                if (config.sourceWorld.get().equals(MaterialSourceWorld.overworld)) {
                    generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
                }
                if (config.sourceWorld.get().equals(MaterialSourceWorld.nether)) {
                    generation.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ore);
                }
            } else {
                IGApi.getNewLogger().info("Generate Kaolinite?");
                generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
            }
        }
    }
}
