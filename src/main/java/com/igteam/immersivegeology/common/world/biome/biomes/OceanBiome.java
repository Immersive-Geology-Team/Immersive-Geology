package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.BiomeHelper;
import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.*;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class OceanBiome extends IGBiome
{
	private final float depthMin, depthMax;
	public OceanBiome(OceanType type)
	{
		super(new Builder().category(Category.OCEAN), 0x4E7280, BiomeHelper.getOceanColor(type), RainType.RAIN, BiomeHelper.getOceanRainfall(type), 2f);

		//correct values
		switch(type){
			case DEEP:
				this.depthMin = SEA_LEVEL-80;
				this.depthMax = SEA_LEVEL-75;

				this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
				this.addStructure(Feature.OCEAN_MONUMENT, IFeatureConfig.NO_FEATURE_CONFIG);
				this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, net.minecraft.world.gen.feature.structure.MineshaftStructure.Type.NORMAL));
				this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));
				DefaultBiomeFeatures.addStructures(this);

				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SQUID, 3, 1, 4));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.COD, 15, 3, 6));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SALMON, 15, 1, 5));
			break;
			case NORMAL:
				this.depthMin = SEA_LEVEL-25;
				this.depthMax = SEA_LEVEL-4;
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.DOLPHIN, 2, 1, 2));
			break;
			case WARM:
				this.depthMin = SEA_LEVEL-25;
				this.depthMax = SEA_LEVEL-4;

				this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.WARM, 0.3F, 0.9F));
				this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));

				DefaultBiomeFeatures.func_222296_u(this);
				DefaultBiomeFeatures.addDefaultFlowers(this);
				DefaultBiomeFeatures.func_222348_W(this);
				DefaultBiomeFeatures.addMushrooms(this);
				DefaultBiomeFeatures.addReedsAndPumpkins(this);
				DefaultBiomeFeatures.func_222340_ak(this);
				DefaultBiomeFeatures.func_222320_ai(this);

				this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createDecoratedFeature(Feature.SIMPLE_RANDOM_SELECTOR, new SingleRandomFeature(new Feature[]{Feature.CORAL_TREE, Feature.CORAL_CLAW, Feature.CORAL_MUSHROOM}, new IFeatureConfig[]{IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG, IFeatureConfig.NO_FEATURE_CONFIG}), Placement.TOP_SOLID_HEIGHTMAP_NOISE_BIASED, new TopSolidWithNoiseConfig(20, 400.0D, 0.0D, net.minecraft.world.gen.Heightmap.Type.OCEAN_FLOOR_WG)));
				DefaultBiomeFeatures.func_222309_aj(this);
				this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createDecoratedFeature(Feature.SEA_PICKLE, new CountConfig(20), Placement.CHANCE_TOP_SOLID_HEIGHTMAP, new ChanceConfig(16)));
				DefaultBiomeFeatures.addFreezeTopLayer(this);

				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SQUID, 5, 1, 4));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.DOLPHIN, 2, 1, 2));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.PUFFERFISH, 15, 1, 3));
			break;
			case DEEP_VOLCANIC:
				this.depthMin = SEA_LEVEL-80;
				this.depthMax = SEA_LEVEL-70;

				DefaultBiomeFeatures.addStructures(this);
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.DOLPHIN, 2, 1, 2));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.PUFFERFISH, 15, 1, 3));

			break;
			case DEEP_FROZEN:
				this.depthMin = SEA_LEVEL-80;
				this.depthMax = SEA_LEVEL-75;

				this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
				this.addStructure(Feature.OCEAN_MONUMENT, IFeatureConfig.NO_FEATURE_CONFIG);
				this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, net.minecraft.world.gen.feature.structure.MineshaftStructure.Type.NORMAL));
				this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));

				DefaultBiomeFeatures.addIcebergs(this);
				DefaultBiomeFeatures.addMonsterRooms(this);
				DefaultBiomeFeatures.addBlueIce(this);
				DefaultBiomeFeatures.func_222296_u(this);
				DefaultBiomeFeatures.addDefaultFlowers(this);
				DefaultBiomeFeatures.func_222348_W(this);
				DefaultBiomeFeatures.addMushrooms(this);
				DefaultBiomeFeatures.addReedsAndPumpkins(this);
				DefaultBiomeFeatures.addSprings(this);
				DefaultBiomeFeatures.addFreezeTopLayer(this);
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SQUID, 1, 1, 4));
				this.addSpawn(EntityClassification.WATER_CREATURE, new SpawnListEntry(EntityType.SALMON, 15, 1, 5));
				this.addSpawn(EntityClassification.CREATURE, new SpawnListEntry(EntityType.POLAR_BEAR, 1, 1, 2));
			break;
			case EDGE:
				//Help smooth the transition between oceans
				this.depthMin = SEA_LEVEL-70;
				this.depthMax = SEA_LEVEL-50;
			break;
			default:
				this.depthMin = SEA_LEVEL-25;
				this.depthMax = SEA_LEVEL-4;
			break;
		}

		this.addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.DROWNED, 5, 1, 1));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.WITCH, 5, 1, 1));
		DefaultBiomeFeatures.addKelp(this);
		IGDefaultBiomeFeatures.addOceanCarvers(this);
	}

	@Override
	public ISurfaceBuilder getIGSurfaceBuilder()
	{
		return ISurfaceBuilder.OCEAN;
	}
	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		// Uses domain warping to achieve a swirly hills effect
		final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
		final INoise2D warpZ = new SimplexNoise2D(seed+1).octaves(4).spread(0.1f).scaled(-30, 30);
		return new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4?x-0.8f: -x).scaled(-0.4f, 0.8f, depthMin, depthMax);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		return Blocks.GRAVEL.getDefaultState();
	}
}