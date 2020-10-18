package com.igteam.immersivegeology.common.world.biome;

import com.igteam.immersivegeology.common.world.gen.feature.IGFeatures;
import com.igteam.immersivegeology.common.world.gen.feature.MossFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.*;

public class IGDefaultBiomeFeatures
{
	public static void addCarvers(Biome biomeIn)
	{
		biomeIn.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biomeIn)
	{
		biomeIn.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.02F)));
		biomeIn.addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(WorldCarver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
	}

	//Population event
	public static void addTrees(Biome biomeIn)
	{
		biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_SELECTOR, new MultipleRandomFeatureConfig(new Feature[]{Feature.BIRCH_TREE, Feature.FANCY_TREE}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.2F, 0.1F}, Feature.NORMAL_TREE, IFeatureConfig.NO_FEATURE_CONFIG), Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(4, 0.1F, 1)));
	}

	public static void addMossLayers(Biome biomeIn){
		biomeIn.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Biome.createDecoratedFeature(IGFeatures.MOSS_LAYER, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
	}

	public static void addCaveFeatures(Biome biomeIn){
		biomeIn.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(IGFeatures.CAVE_FEATURES, IFeatureConfig.NO_FEATURE_CONFIG, Placement.CARVING_MASK, new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.015f)));
	}

	public static void addNetherCaveFeatures(Biome biomeIn){
		biomeIn.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(IGFeatures.CAVE_FEATURES, IFeatureConfig.NO_FEATURE_CONFIG, Placement.CARVING_MASK, new CaveEdgeConfig(GenerationStage.Carving.AIR, 0.0015f)));
		biomeIn.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Biome.createDecoratedFeature(IGFeatures.NETHER_CAVE_FEATURES, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 255)));
	}

	public static void addLeafShurbs(Biome biomeIn)
	{
		biomeIn.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_SELECTOR, new MultipleRandomFeatureConfig(new Feature[]{Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH, Feature.JUNGLE_GROUND_BUSH}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}, new float[]{0.1F, 0.5F, 0.33333334F}, Feature.JUNGLE_TREE, IFeatureConfig.NO_FEATURE_CONFIG), Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(50, 0.1F, 1)));
	}
}
