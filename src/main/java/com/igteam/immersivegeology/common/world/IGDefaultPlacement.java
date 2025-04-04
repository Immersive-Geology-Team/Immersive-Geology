/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.placements.IGCountPlacement;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class IGDefaultPlacement extends PlacementFilter
{
	public static final Codec<IGDefaultPlacement> PLACEMENT_CODEC;

	private final IWorldGenConfig entry;
	public IGDefaultPlacement(IWorldGenConfig entry) {
		this.entry = entry;
	}

	public boolean canPlaceVein(ChunkPos pos, long level_seed, OreConfig config) {
		if (config.veinSize.get() <= 0) return false;

		RandomSource random = new XoroshiroRandomSource(
				level_seed ^ (long)pos.x * 61728364132L,
				entry.seed() ^ (long)pos.z * 16298364123L
		);

		int chance_max = 2_000_000;
		return random.nextInt(chance_max) < config.generationChance.get();
	}

	public boolean canSpawnAt(Holder<Biome> biome) {
		float biomeTemp = biome.value().getBaseTemperature();
		float biomeDownfall = biome.value().getModifiedClimateSettings().downfall();
		IGServerConfig.Ores.OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config == null)
		{
			IGServerConfig.Evaporates.EvaporateConfig econfig = IGServerConfig.EVAPORITES.evaporates.get(entry);
			return econfig.veinsPerChunk.get() > 0;
		}
		if(!config.canSpawn.get()) return false;
		return biomeTemp >= config.min_temp.get() &&
				biomeTemp <= config.max_temp.get() &&
				biomeDownfall >= config.min_downfall.get() &&
				biomeDownfall <= config.max_downfall.get();
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_DEFAULT_PLACEMENT.get();
	}

	static {
		PLACEMENT_CODEC = IWorldGenConfig.CODEC.xmap(IGDefaultPlacement::new, (p) -> {
			return p.entry;
		});
	}

	@Override
	protected boolean shouldPlace(PlacementContext ctx, RandomSource rnd, BlockPos pos)
	{
		long seed = ctx.getLevel().getSeed();
		rnd.setSeed(seed);

		OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config == null)
		{
			IGServerConfig.Evaporates.EvaporateConfig econfig = IGServerConfig.EVAPORITES.evaporates.get(entry);
			return true;
		}
		Holder<Biome> biome = ctx.getLevel().getBiome(pos);
		boolean isOverworld = biome.containsTag(BiomeTags.IS_OVERWORLD);
		boolean isEnd = biome.containsTag(BiomeTags.IS_END);
		boolean isNether = biome.containsTag(BiomeTags.IS_NETHER);
		if(isOverworld || isNether || isEnd)
		{
			boolean possiblePlace = canPlaceVein(new ChunkPos(pos), seed, config) && canSpawnAt(ctx.getLevel().getBiome(pos));
			MaterialHelper material = entry.instance();
			boolean canSpawnOverworld = material.acceptableStoneType(StoneEnum.MCStone);
			boolean canSpawnNether = material.acceptableStoneType(StoneEnum.MCNetherrack);
			boolean canSpawnEnd = material.acceptableStoneType(StoneEnum.MCEndStone);

			return possiblePlace && ((canSpawnOverworld && isOverworld) || (canSpawnNether && isNether) || (canSpawnEnd && isEnd));
		}
		// Some other Dimension or TFC
		return canPlaceVein(new ChunkPos(pos), seed, config) && canSpawnAt(ctx.getLevel().getBiome(pos));
	}
}
