package com.igteam.immersivegeology.common.world.biome.biomes;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGDefaultBiomeFeatures;
import com.igteam.immersivegeology.common.world.gen.surface.util.SurfaceBlockType;
import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

public class BadlandsBiome extends IGBiome
{
	public BadlandsBiome()
	{
		super(new IGBiome.Builder().category(Category.MESA).precipitation(RainType.RAIN).downfall(0.01f)
				.temperature(0.85f), 0.85f, 0.01f);
		this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.MESA));
		this.addStructure(Feature.STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);

		IGDefaultBiomeFeatures.addCarvers(this);
		DefaultBiomeFeatures.func_222308_M(this);
		DefaultBiomeFeatures.addStructures(this);
		DefaultBiomeFeatures.addMonsterRooms(this);
		DefaultBiomeFeatures.addStructures(this);

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
		// Normal flat noise, lowered by inverted power-ridge noise, looks like badlands
		final INoise2D ridgeNoise = new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f)
				.map(x -> 1.3f*-(x > 0?(float)Math.pow(x, 3.2f): 0.5f*x)).scaled(-1f, 0.3f, -1f, 1f)
				.terraces(16).scaled(-20, 0);
		return new SimplexNoise2D(seed).octaves(6).spread(0.08f).scaled(SEA_LEVEL+22, SEA_LEVEL+32).add(ridgeNoise);
	}

	@Override
	public BlockState returnBlockType(SurfaceBlockType part, float chunkTemp, float chunkRain)
	{
		// Chunk Temp and Rainfall for Surface Rock/Grass states
		switch(part)
		{
			case grass:
				return Blocks.RED_SAND.getDefaultState();
			case dirt:
				return Blocks.RED_SANDSTONE.getDefaultState();
			default:
				return Blocks.RED_SANDSTONE.getDefaultState();
		}
	}
}
