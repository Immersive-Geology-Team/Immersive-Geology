package com.igteam.immersivegeology.common.world.biome.biomes.nether;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.feature.IGFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class NetherOcean extends IGBiome
{
	public NetherOcean()
	{
		super(new Builder().category(Category.NETHER).precipitation(RainType.NONE).downfall(0f)
				.temperature(1f), 1f, 0f);

		this.addStructure(Feature.STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
		this.addStructure(IGFeatures.IMMERSIVE_NETHER_BRIDGE, IFeatureConfig.NO_FEATURE_CONFIG);

		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createDecoratedFeature(Feature.SPRING_FEATURE, new LiquidsConfig(Fluids.LAVA.getDefaultState()), Placement.COUNT_VERY_BIASED_RANGE, new CountRangeConfig(20, 8, 16, 256)));
		DefaultBiomeFeatures.addMushrooms(this);
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(IGFeatures.IMMERSIVE_NETHER_BRIDGE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.NETHER_SPRING, new HellLavaConfig(false), Placement.COUNT_RANGE, new CountRangeConfig(8, 4, 8, 253)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.HELL_FIRE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.HELL_FIRE, new FrequencyConfig(10)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.GLOWSTONE_BLOB, IFeatureConfig.NO_FEATURE_CONFIG, Placement.LIGHT_GEM_CHANCE, new FrequencyConfig(10)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.GLOWSTONE_BLOB, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_RANGE, new CountRangeConfig(10, 0, 0, 253)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.BUSH, new BushConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Placement.CHANCE_RANGE, new ChanceRangeConfig(0.5F, 0, 0, 253)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.BUSH, new BushConfig(Blocks.RED_MUSHROOM.getDefaultState()), Placement.CHANCE_RANGE, new ChanceRangeConfig(0.5F, 0, 0, 253)));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, createDecoratedFeature(Feature.NETHER_SPRING, new HellLavaConfig(true), Placement.COUNT_RANGE, new CountRangeConfig(16, 10, 20, 253)));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.GHAST, 50, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new SpawnListEntry(EntityType.ENDERMAN, 1, 4, 4));
		IGDefaultBiomeFeatures.addCarvers(this);
		IGDefaultBiomeFeatures.addNetherCaveFeatures(this);
	}

	@OnlyIn(Dist.CLIENT)
	public int getFoliageColor(BlockPos pos)
	{
		return 10387789;
	}

	@OnlyIn(Dist.CLIENT)
	public int getGrassColor(BlockPos pos)
	{
		return 9470285;
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
		final INoise2D warpZ = new SimplexNoise2D(seed+1).octaves(4).spread(0.1f).scaled(-30, 30);
		return new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).warped(warpX, warpZ).map(x -> x > 0.4?x-0.8f: -x).scaled(-0.4f, 0.8f, SEA_LEVEL-4, SEA_LEVEL-22).terraces(6);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		return IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Netherrack.material).getDefaultState();
	}
}
