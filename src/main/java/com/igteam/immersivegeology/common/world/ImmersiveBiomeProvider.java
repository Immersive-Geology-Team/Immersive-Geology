package com.igteam.immersivegeology.common.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.storage.WorldInfo;

public class ImmersiveBiomeProvider extends BiomeProvider {
	 private final Layer genBiomes;
	   private final Layer biomeFactoryLayer;
	   private final Biome[] biomes = new Biome[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};

	   public ImmersiveBiomeProvider(OverworldBiomeProviderSettings settingsProvider) {
	      WorldInfo worldinfo = settingsProvider.getWorldInfo();
	      OverworldGenSettings overworldgensettings = settingsProvider.getGeneratorSettings();
	      Layer[] alayer = LayerUtil.buildOverworldProcedure(worldinfo.getSeed(), worldinfo.getGenerator(), overworldgensettings);
	      this.genBiomes = alayer[0];
	      this.biomeFactoryLayer = alayer[1];
	   }

	   /**
	    * Gets the biome from the provided coordinates
	    */
	   public Biome getBiome(int x, int y) {
	      return this.biomeFactoryLayer.func_215738_a(x, y);
	   }

	   public Biome func_222366_b(int p_222366_1_, int p_222366_2_) {
	      return this.genBiomes.func_215738_a(p_222366_1_, p_222366_2_);
	   }

	   public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag) {
	      return this.biomeFactoryLayer.generateBiomes(x, z, width, length);
	   }

	   public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength) {
	      int i = centerX - sideLength >> 2;
	      int j = centerZ - sideLength >> 2;
	      int k = centerX + sideLength >> 2;
	      int l = centerZ + sideLength >> 2;
	      int i1 = k - i + 1;
	      int j1 = l - j + 1;
	      Set<Biome> set = Sets.newHashSet();
	      Collections.addAll(set, this.genBiomes.generateBiomes(i, j, i1, j1));
	      return set;
	   }

	   @Nullable
	   public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
	      int i = x - range >> 2;
	      int j = z - range >> 2;
	      int k = x + range >> 2;
	      int l = z + range >> 2;
	      int i1 = k - i + 1;
	      int j1 = l - j + 1;
	      Biome[] abiome = this.genBiomes.generateBiomes(i, j, i1, j1);
	      BlockPos blockpos = null;
	      int k1 = 0;

	      for(int l1 = 0; l1 < i1 * j1; ++l1) {
	         int i2 = i + l1 % i1 << 2;
	         int j2 = j + l1 / i1 << 2;
	         if (biomes.contains(abiome[l1])) {
	            if (blockpos == null || random.nextInt(k1 + 1) == 0) {
	               blockpos = new BlockPos(i2, 0, j2);
	            }

	            ++k1;
	         }
	      }

	      return blockpos;
	   }

	   public boolean hasStructure(Structure<?> structureIn) {
	      return this.hasStructureCache.computeIfAbsent(structureIn, (p_205006_1_) -> {
	         for(Biome biome : this.biomes) {
	            if (biome.hasStructure(p_205006_1_)) {
	               return true;
	            }
	         }

	         return false;
	      });
	   }

	   public Set<BlockState> getSurfaceBlocks() {
	      if (this.topBlocksCache.isEmpty()) {
	         for(Biome biome : this.biomes) {
	            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
	         }
	      }

	      return this.topBlocksCache;
	   }

}
