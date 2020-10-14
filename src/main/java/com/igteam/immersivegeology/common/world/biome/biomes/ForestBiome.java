package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.BiomeHelper;
import com.igteam.immersivegeology.common.world.biome.biomes.helpers.ForestType;
import com.igteam.immersivegeology.common.world.chunk.IGChunkCapability;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class ForestBiome extends IGBiome
{
	private final float minHeight;
	private final float maxHeight;
	private final ForestType forestType;

	public ForestBiome(ForestType type, float minHeight, float maxHeight)
	{
		super(new Builder().category(Category.FOREST).precipitation(BiomeHelper.getForestRain(type)).downfall(.5f).temperature(BiomeHelper.getForestTemperature(type)), BiomeHelper.getForestTemperature(type), 0.1f);

		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		forestType = type;
		switch(type){
			case SNOWY:
				this.addStructure(Feature.IGLOO, IFeatureConfig.NO_FEATURE_CONFIG);
				this.addStructure(Feature.MINESHAFT,new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
				this.addStructure(Feature.STRONGHOLD,IFeatureConfig.NO_FEATURE_CONFIG);
				IGDefaultBiomeFeatures.addCarvers(this);
				DefaultBiomeFeatures.addStructures(this);
				DefaultBiomeFeatures.addLakes(this);
				DefaultBiomeFeatures.addIcebergs(this);
				DefaultBiomeFeatures.addFreezeTopLayer(this);
				DefaultBiomeFeatures.addMonsterRooms(this);
				DefaultBiomeFeatures.addTaigaLargeFerns(this);
				DefaultBiomeFeatures.addTaigaConifers(this);
				DefaultBiomeFeatures.addMushrooms(this);
				DefaultBiomeFeatures.addReedsAndPumpkins(this);
				DefaultBiomeFeatures.addSparseBerryBushes(this);
				DefaultBiomeFeatures.addFreezeTopLayer(this);
				this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
			break;
			case SWEDISH:
				this.addStructure(Feature.MINESHAFT,new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
				this.addStructure(Feature.STRONGHOLD,IFeatureConfig.NO_FEATURE_CONFIG);
				IGDefaultBiomeFeatures.addCarvers(this);
				DefaultBiomeFeatures.addMonsterRooms(this);
				DefaultBiomeFeatures.addTaigaLargeFerns(this);
				DefaultBiomeFeatures.addTaigaConifers(this);
				DefaultBiomeFeatures.addTaigaConifers(this);
				DefaultBiomeFeatures.addTaigaConifers(this);
				DefaultBiomeFeatures.addReedsAndPumpkins(this);
				DefaultBiomeFeatures.addMushrooms(this);
				DefaultBiomeFeatures.addBerryBushes(this);
				IGDefaultBiomeFeatures.addMossLayers(this);

				this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.WOLF, 8, 4, 4));
				this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.RABBIT, 4, 2, 3));
				this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.FOX, 8, 2, 4));
			break;
		}

		this.addSpawn(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
	}

	@Nonnull
	@Override
	public INoise2D createNoiseLayer(long seed)
	{
		return new SimplexNoise2D(seed).octaves(4).spread(0.06f).scaled(SEA_LEVEL+minHeight, SEA_LEVEL+maxHeight);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		switch(part)
		{
			case grass:
				return Blocks.GRASS_BLOCK.getDefaultState();
			default:
				return Blocks.DIRT.getDefaultState();
		}
	}

	public ForestType getType(){
		return this.forestType;
	}
}