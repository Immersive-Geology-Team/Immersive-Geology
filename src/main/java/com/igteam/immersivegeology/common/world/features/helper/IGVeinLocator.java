/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IGDefaultPlacement;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.compat.IGTFCWorld;
import com.igteam.immersivegeology.common.world.features.IGOreFeature;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Seed driven lookup for {@link IGOreFeature} veins.
 * <p>
 * Vein placement is decided by {@link IGDefaultPlacement}: a per chunk roll seeded from the world seed and the
 * material, plus a biome climate check. Both of those are pure functions of data we can obtain without loading -
 * let alone generating - a single chunk, so a whole search radius can be filtered down to a handful of candidate
 * chunks off the server thread before anything touches the world.
 * <p>
 * The one part of placement that genuinely needs blocks is {@link IGOreGenUtils#isVeinWorthwhile}, which asks
 * whether the stone in the area accepts the ore. That check is what {@code absolute} mode pays for.
 */
public final class IGVeinLocator
{
	private IGVeinLocator()
	{
	}

	/** How far, in chunks, {@link IGOreFeature#placeVein} writes either side of the chunk a vein is rolled for. */
	public static final int VEIN_CHUNK_SPREAD = 1;

	private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
	private static final int SCAN_THREADS = Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()-1));
	private static final ExecutorService SCAN_POOL = Executors.newFixedThreadPool(SCAN_THREADS, task -> {
		Thread thread = new Thread(task, "IG Vein Scan #"+THREAD_COUNTER.incrementAndGet());
		thread.setDaemon(true);
		// Never outrank the server thread; a locate is not worth a tick.
		thread.setPriority(Thread.NORM_PRIORITY-1);
		return thread;
	});

	/** Why a prediction came back with no candidates, so the command can say something better than "not found". */
	public enum Outcome
	{
		FOUND,
		/** The material's {@code canSpawn} or {@code vein_size} config rules it out entirely. */
		DISABLED,
		/** The material's dimension whitelist does not cover the dimension being searched. */
		WRONG_DIMENSION,
		/** Generation is possible, the seed just never rolls this material inside the radius. */
		NOTHING_ROLLED
	}

	/**
	 * @param minY       lowest level any participating material is configured to reach, so a scan need look no lower
	 * @param candidates chunks a vein is rolled for, nearest to the search origin first
	 */
	public record Prediction(int minY, int maxY, List<ChunkPos> candidates, Outcome outcome)
	{
		public boolean isEmpty()
		{
			return candidates.isEmpty();
		}
	}

	/** One material's placement rule, resolved once so a slice can roll it against many chunks cheaply. */
	private record MaterialRoll(IWorldGenConfig entry, OreConfig config, IGDefaultPlacement placement)
	{
		/** null when the material cannot generate in this dimension at all. */
		@Nullable
		static MaterialRoll of(ServerLevel level, IWorldGenConfig entry)
		{
			OreConfig config = IGServerConfig.ORES.ores.get(entry);
			if(config==null||!config.canSpawn.get()||config.veinSize.get() <= 0) return null;
			if(!canGenerateIn(level, entry)) return null;
			return new MaterialRoll(entry, config, new IGDefaultPlacement(entry));
		}
	}

	/**
	 * Filters every chunk in the radius down to those a vein of {@code entry} is actually rolled for, spread across
	 * the scan pool. No chunk is loaded or generated, so the cost is a handful of arithmetic ops and one biome
	 * sample per chunk.
	 */
	public static CompletableFuture<Prediction> predictAsync(ServerLevel level, IWorldGenConfig entry, ChunkPos origin, int radius)
	{
		if(IGServerConfig.disable_mineral_generation.get()) return refused(Outcome.DISABLED);

		OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config==null||!config.canSpawn.get()||config.veinSize.get() <= 0) return refused(Outcome.DISABLED);
		if(!canGenerateIn(level, entry)) return refused(Outcome.WRONG_DIMENSION);

		return predict(level, List.of(new MaterialRoll(entry, config, new IGDefaultPlacement(entry))), origin, radius);
	}

	/**
	 * The same, for any Immersive Geology deposit at all: every material that can generate in this dimension,
	 * rolled together. Sharing one biome sample per chunk across the whole set makes this barely more expensive
	 * than searching for a single material - the roll itself is only arithmetic.
	 */
	public static CompletableFuture<Prediction> predictAnyAsync(ServerLevel level, ChunkPos origin, int radius)
	{
		if(IGServerConfig.disable_mineral_generation.get()) return refused(Outcome.DISABLED);

		List<MaterialRoll> materials = new ArrayList<>();
		for(IWorldGenConfig entry : IGServerConfig.ORES.ores.keySet())
		{
			MaterialRoll roll = MaterialRoll.of(level, entry);
			if(roll!=null) materials.add(roll);
		}
		// rollSlice takes the first material that rolls for a chunk, so this order decides who wins a chunk two
		// materials both roll for. The config map is a HashMap keyed on enum identity, whose iteration order is
		// fixed within a run but not across launches, so it is sorted into one the seed can be held to.
		materials.sort(Comparator.comparing(roll -> roll.entry().name()));
		if(materials.isEmpty()) return refused(Outcome.WRONG_DIMENSION);

		return predict(level, materials, origin, radius);
	}

	/**
	 * Every chunk in the radius the world seed rolls a deposit for, with the material it rolled - the data a metal
	 * detector's display is built from.
	 * <p>
	 * This reads no blocks at all, so it works over terrain that has never been generated. What it reports is where
	 * the seed puts deposits, not what is still in the ground: a vein someone has already mined out still shows.
	 */
	public static CompletableFuture<List<ChunkDeposit>> scanDepositsAsync(ServerLevel level, ChunkPos origin, int radius)
	{
		if(IGServerConfig.disable_mineral_generation.get()) return CompletableFuture.completedFuture(List.of());

		List<MaterialRoll> materials = new ArrayList<>();
		for(IWorldGenConfig entry : IGServerConfig.ORES.ores.keySet())
		{
			MaterialRoll roll = MaterialRoll.of(level, entry);
			if(roll!=null) materials.add(roll);
		}
		// rollSlice takes the first material that rolls for a chunk, so this order decides who wins a chunk two
		// materials both roll for. The config map is a HashMap keyed on enum identity, whose iteration order is
		// fixed within a run but not across launches, so it is sorted into one the seed can be held to.
		materials.sort(Comparator.comparing(roll -> roll.entry().name()));
		if(materials.isEmpty()) return CompletableFuture.completedFuture(List.of());

		return rollAll(level, materials, origin, radius);
	}

	/** Fans the roll out across the scan pool without ever blocking one of its threads. */
	private static CompletableFuture<List<ChunkDeposit>> rollAll(ServerLevel level, List<MaterialRoll> materials,
																ChunkPos origin, int radius)
	{
		long seed = level.getSeed();
		int span = radius*2+1;
		int searched = span*span;

		int slices = Math.max(1, Math.min(SCAN_THREADS, searched));
		int sliceSize = (searched+slices-1)/slices;
		List<CompletableFuture<List<ChunkDeposit>>> futures = new ArrayList<>(slices);
		for(int slice = 0; slice < slices; slice++)
		{
			int from = slice*sliceSize;
			int to = Math.min(searched, from+sliceSize);
			if(from >= to) break;
			futures.add(CompletableFuture.supplyAsync(
					() -> rollSlice(level, materials, origin, radius, span, seed, from, to), SCAN_POOL));
		}

		CompletableFuture<?>[] pending = futures.toArray(new CompletableFuture<?>[0]);
		return CompletableFuture.allOf(pending).handle((ignored, error) -> {
			if(error!=null) IGLib.IG_LOGGER.warn("Deposit scan failed: {}", error.toString());
			List<ChunkDeposit> deposits = new ArrayList<>();
			for(CompletableFuture<List<ChunkDeposit>> future : futures)
			{
				if(!future.isCompletedExceptionally()) deposits.addAll(future.join());
			}
			return deposits;
		});
	}

	private static CompletableFuture<Prediction> refused(Outcome outcome)
	{
		return CompletableFuture.completedFuture(new Prediction(0, 0, List.of(), outcome));
	}

	/**
	 * Nothing here ever blocks on the pool - the fan-in is a {@code handle} - so several searches can be in flight
	 * at once without starving each other.
	 */
	private static CompletableFuture<Prediction> predict(ServerLevel level, List<MaterialRoll> materials,
														 ChunkPos origin, int radius)
	{
		long seed = level.getSeed();
		int span = radius*2+1;
		int searched = span*span;

		// The scan has to cover every participating material's height range.
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for(MaterialRoll material : materials)
		{
			minY = Math.min(minY, IGServerConfig.getOrDefault(material.config().minY));
			maxY = Math.max(maxY, IGServerConfig.getOrDefault(material.config().maxY));
		}

		int slices = Math.max(1, Math.min(SCAN_THREADS, searched));
		int sliceSize = (searched+slices-1)/slices;
		List<CompletableFuture<List<ChunkDeposit>>> futures = new ArrayList<>(slices);
		for(int slice = 0; slice < slices; slice++)
		{
			int from = slice*sliceSize;
			int to = Math.min(searched, from+sliceSize);
			if(from >= to) break;
			futures.add(CompletableFuture.supplyAsync(
					() -> rollSlice(level, materials, origin, radius, span, seed, from, to), SCAN_POOL));
		}

		int veinMinY = minY;
		int veinMaxY = maxY;
		CompletableFuture<?>[] pending = futures.toArray(new CompletableFuture<?>[0]);
		return CompletableFuture.allOf(pending).handle((ignored, error) -> {
			if(error!=null) IGLib.IG_LOGGER.warn("Vein scan prediction failed: {}", error.toString());
			List<ChunkPos> candidates = new ArrayList<>();
			for(CompletableFuture<List<ChunkDeposit>> future : futures)
			{
				if(future.isCompletedExceptionally()) continue;
				for(ChunkDeposit deposit : future.join()) candidates.add(deposit.chunk());
			}
			candidates.sort(Comparator.comparingLong(chunk -> distanceSq(origin, chunk)));
			return new Prediction(veinMinY, veinMaxY, List.copyOf(candidates),
					candidates.isEmpty()?Outcome.NOTHING_ROLLED: Outcome.FOUND);
		});
	}

	/** A chunk the world seed rolls a deposit for, and which material it rolled. */
	public record ChunkDeposit(ChunkPos chunk, IWorldGenConfig entry)
	{
	}

	/** Tests chunk indices {@code [from, to)} of the {@code span * span} square centred on {@code origin}. */
	private static List<ChunkDeposit> rollSlice(ServerLevel level, List<MaterialRoll> materials, ChunkPos origin,
											   int radius, int span, long seed, int from, int to)
	{
		List<ChunkDeposit> hits = new ArrayList<>();
		for(int index = from; index < to; index++)
		{
			ChunkPos chunk = new ChunkPos(origin.x+(index/span)-radius, origin.z+(index%span)-radius);

			// The biome the generator would pick here, read straight from the noise router rather than from a
			// chunk. Datapack biome modifiers can still shuffle this after the fact, which is what the block scan
			// that follows catches. Sampling it is the expensive part of the roll, so it is done once and reused
			// across every material.
			Holder<Biome> biome = level.getUncachedNoiseBiome(
					QuartPos.fromBlock(chunk.getMinBlockX()), 0, QuartPos.fromBlock(chunk.getMinBlockZ()));
			boolean isEnd = biome.containsTag(BiomeTags.IS_END);

			for(MaterialRoll material : materials)
			{
				if(rollsVein(material, chunk, seed, biome, isEnd))
				{
					hits.add(new ChunkDeposit(chunk, material.entry()));
					break;
				}
			}
		}
		return hits;
	}

	/**
	 * Mirrors {@link IGDefaultPlacement#shouldPlace} minus the block level {@code isVeinWorthwhile} pass, which is
	 * the only part that needs a generated chunk.
	 */
	private static boolean rollsVein(MaterialRoll material, ChunkPos chunk, long seed, Holder<Biome> biome, boolean isEnd)
	{
		IGDefaultPlacement placement = material.placement();
		OreConfig config = material.config();
		boolean placed = isEnd?placement.canPlaceVeinEnd(chunk, seed, config): placement.canPlaceVein(chunk, seed, config);
		return placed&&placement.canSpawnAt(biome);
	}

	/**
	 * Whether this material's own placement can run in this dimension: either the dimension whitelist names it, or
	 * the TFC overworld override in {@link IGTFCWorld} lets it in anyway.
	 */
	public static boolean canGenerateIn(ServerLevel level, IWorldGenConfig entry)
	{
		OreConfig config = IGServerConfig.ORES.ores.get(entry);
		if(config==null) return false;

		ResourceLocation dimension = level.dimension().location();
		boolean whitelisted = config.dimension_whitelist.get().stream()
				.map(ResourceLocation::new)
				.anyMatch(dimension::equals);
		if(whitelisted) return true;

		return IGTFCWorld.isOverworld(level)&&IGTFCWorld.overridesDimensionWhitelist(level)
				&&IGTFCWorld.canHostInOverworld(entry.instance());
	}

	/**
	 * Whether the rock this dimension is built from can hold the material even though nothing lets the material's
	 * own placement run here.
	 * <p>
	 * A vanilla overworld is {@link StoneFormation#MINECRAFT_STONE} throughout, so the sulphide minerals - which
	 * accept metamorphic and igneous rock but not Minecraft stone - really are confined to the Nether there, which
	 * is exactly what their whitelist says. A TFC overworld is built out of those very formations, so with the
	 * override switched off they can still turn up in it: not from their own placement, but as associate materials
	 * carried in by whatever vein generated nearby.
	 * <p>
	 * Nothing about that is visible to the seed roll, so a search in this situation has to look at the blocks
	 * themselves rather than predict.
	 */
	public static boolean canLocalStoneHost(ServerLevel level, IWorldGenConfig entry)
	{
		return IGTFCWorld.isOverworld(level)&&IGTFCWorld.isTFCWorld(level)
				&&IGTFCWorld.canHostInOverworld(entry.instance());
	}

	/** The entry of {@code packed} closest to {@code target}. */
	@Nullable
	private static BlockPos nearest(LongArrayList packed, BlockPos target)
	{
		long best = 0;
		long bestDistance = Long.MAX_VALUE;
		for(int i = 0; i < packed.size(); i++)
		{
			long position = packed.getLong(i);
			long dx = BlockPos.getX(position)-target.getX();
			long dy = BlockPos.getY(position)-target.getY();
			long dz = BlockPos.getZ(position)-target.getZ();
			long distance = dx*dx+dy*dy+dz*dz;
			if(distance < bestDistance)
			{
				bestDistance = distance;
				best = position;
			}
		}
		return bestDistance==Long.MAX_VALUE?null: BlockPos.of(best);
	}

	/**
	 * How many blocks in {@code chunk} match, within the given height range.
	 * <p>
	 * The per-section palette check in front of the loop is the cheap gate. A palette can hold entries for block
	 * types that are no longer present - mining the last one out does not rebuild it until the chunk reloads - so
	 * a hit only means "possible", never "certain". It is reliable in the other direction though: a palette that
	 * has never contained the block cannot be hiding one, so a miss safely skips 4096 lookups.
	 *
	 * @return the exact count, which is 0 when the palette lied
	 */
	public static int countMatching(ChunkAccess chunk, Predicate<BlockState> matches, int minY, int maxY)
	{
		int lowestY = Math.max(minY, chunk.getMinBuildHeight());
		int highestY = Math.min(maxY, chunk.getMaxBuildHeight()-1);
		if(lowestY > highestY) return 0;

		int sectionMin = chunk.getSectionIndex(lowestY);
		int sectionMax = chunk.getSectionIndex(highestY);
		int found = 0;

		for(int sectionIndex = sectionMin; sectionIndex <= sectionMax; sectionIndex++)
		{
			LevelChunkSection section = chunk.getSection(sectionIndex);
			if(section.hasOnlyAir()||!section.maybeHas(matches)) continue;

			int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
			int fromY = Math.max(sectionMinY, lowestY);
			int toY = Math.min(sectionMinY+16, highestY+1);
			for(int y = fromY; y < toY; y++)
			{
				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{
						if(matches.test(section.getBlockState(x, y&15, z))) found++;
					}
				}
			}
		}
		return found;
	}

	/** A deposit found inside a chunk: the block best representing its middle, and which mineral it is. */
	public record OreHit(BlockPos position, @Nullable MaterialInterface<?> material)
	{
	}

	/**
	 * The deposits present in {@code chunk}, one entry per distinct ore material.
	 * <p>
	 * Splitting by material is what makes a search that accepts any mineral useful: the centre of mass of two
	 * different minerals sharing a chunk is a point in between that belongs to neither of them, and whichever ore
	 * happens to sit nearest it would get reported under the wrong name. Grouped, every entry is the middle of one
	 * actual deposit and is named for what it is.
	 *
	 * @return empty when the chunk holds nothing matching {@code matches} in the given height range
	 */
	public static List<OreHit> locateOreCentres(ChunkAccess chunk, Predicate<BlockState> matches, int minY, int maxY)
	{
		int lowestY = Math.max(minY, chunk.getMinBuildHeight());
		int highestY = Math.min(maxY, chunk.getMaxBuildHeight()-1);
		if(lowestY > highestY) return List.of();

		int sectionMin = chunk.getSectionIndex(lowestY);
		int sectionMax = chunk.getSectionIndex(highestY);
		ChunkPos chunkPos = chunk.getPos();
		Map<MaterialInterface<?>, LongArrayList> byMaterial = new HashMap<>();

		for(int sectionIndex = sectionMin; sectionIndex <= sectionMax; sectionIndex++)
		{
			LevelChunkSection section = chunk.getSection(sectionIndex);
			if(section.hasOnlyAir()||!section.maybeHas(matches)) continue;

			int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
			int fromY = Math.max(sectionMinY, lowestY);
			int toY = Math.min(sectionMinY+16, highestY+1);
			for(int y = fromY; y < toY; y++)
			{
				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{
						BlockState state = section.getBlockState(x, y&15, z);
						if(!matches.test(state)) continue;

						MaterialInterface<?> material = state.getBlock() instanceof IOreBlock ore
								?ore.getOreMaterial(): null;
						byMaterial.computeIfAbsent(material, key -> new LongArrayList())
								.add(BlockPos.asLong(chunkPos.getMinBlockX()+x, y, chunkPos.getMinBlockZ()+z));
					}
				}
			}
		}

		List<OreHit> hits = new ArrayList<>(byMaterial.size());
		byMaterial.forEach((material, positions) -> {
			BlockPos centre = centreOf(positions);
			if(centre!=null) hits.add(new OreHit(centre, material));
		});
		return hits;
	}

	/** Centre of mass of {@code positions}, snapped onto the nearest one so the answer always lands on ore. */
	@Nullable
	private static BlockPos centreOf(LongArrayList positions)
	{
		if(positions.isEmpty()) return null;

		long sumX = 0, sumY = 0, sumZ = 0;
		for(int i = 0; i < positions.size(); i++)
		{
			long packed = positions.getLong(i);
			sumX += BlockPos.getX(packed);
			sumY += BlockPos.getY(packed);
			sumZ += BlockPos.getZ(packed);
		}

		// The centre of mass of a hollow or forked vein can easily fall in bare stone, so report the ore block
		// closest to it instead.
		BlockPos centre = new BlockPos((int)(sumX/positions.size()), (int)(sumY/positions.size()),
				(int)(sumZ/positions.size()));
		return nearest(positions, centre);
	}


	public static long distanceSq(ChunkPos from, ChunkPos to)
	{
		long dx = (long)from.x-to.x;
		long dz = (long)from.z-to.z;
		return dx*dx+dz*dz;
	}

}
