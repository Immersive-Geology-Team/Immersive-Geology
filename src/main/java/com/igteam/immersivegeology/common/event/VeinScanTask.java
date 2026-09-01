/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator.OreHit;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator.Outcome;
import com.igteam.immersivegeology.common.world.features.helper.IGVeinLocator.Prediction;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class VeinScanTask
{
	private static final long SCAN_BUDGET_NANOS = 5_000_000L;

	private static final int PROGRESS_INTERVAL = 10;

	private static final int CHUNK_LIMIT = 4096;

	private static final int PREFETCH_DEPTH = 16;

	private static final int STALL_LIMIT = 600;

	private static final int DISTINCT_VEIN_DISTANCE = 48;

	private static final TicketType<ChunkPos> SCAN_TICKET =
			TicketType.create("ig_vein_scan", Comparator.comparingLong(ChunkPos::toLong));

	private enum Stage
	{
		PREDICTING,
		SCANNING,
		DONE
	}

	public final CommandSourceStack source;
	public final ServerLevel level;

	@Nullable
	public final MaterialInterface<?> type;
	public final int radius;
	public final boolean absolute;

	public final boolean fullScan;

	public final boolean tfcAssociateFallback;


	public final int wanted;

	private final ChunkPos origin;
	private final Vec3 searchedFrom;
	private final Predicate<BlockState> matcher;
	@Nullable
	private final CompletableFuture<Prediction> prediction;

	private Stage stage;
	private Prediction predicted;
	private int scanMinY;
	private int scanMaxY;
	private List<ChunkPos> queue = List.of();
	private int cursor = 0;


	private final ArrayDeque<ChunkPos> priority = new ArrayDeque<>();

	private final LongOpenHashSet candidateChunks = new LongOpenHashSet();
	private final LongOpenHashSet requested = new LongOpenHashSet();
	private final List<ChunkPos> inFlight = new ArrayList<>();
	private final List<Found> results = new ArrayList<>();
	private int chunksScanned = 0;
	private int ticks = 0;
	private int stalledTicks = 0;
	private boolean hitChunkLimit = false;
	private boolean stalled = false;

	private boolean stopRequesting = false;


	public static VeinScanTask forMaterial(CommandSourceStack source, @NotNull ServerLevel level,
										   @NotNull MaterialInterface<?> type, int radius, boolean absolute, int wanted)
	{
		IWorldGenConfig genConfig = (IWorldGenConfig)type;
		TagKey<Block> tag = type.getBlockMaterialTag();
		// A material that cannot place itself here can still be in the ground as an associate of some other vein,
		// which is what happens to the Nether sulphides in a TFC world while allow_all_in_overworld is off - TFC's
		// rock accepts them even though their own placement never runs. The seed roll cannot see any of that, so
		// there is nothing to predict from and the search has to read the blocks instead.
		boolean tfcFallback = !absolute&&!IGVeinLocator.canGenerateIn(level, genConfig)
				&&IGVeinLocator.canLocalStoneHost(level, genConfig);
		return new VeinScanTask(source, level, type, state -> state.is(tag), radius, absolute, wanted, tfcFallback);
	}

	public static VeinScanTask forAnyDeposit(CommandSourceStack source, @NotNull ServerLevel level, int radius,
											 boolean absolute, int wanted)
	{
		return new VeinScanTask(source, level, null, state -> state.getBlock() instanceof IOreBlock,
				radius, absolute, wanted, false);
	}

	private VeinScanTask(CommandSourceStack source, @NotNull ServerLevel level, @Nullable MaterialInterface<?> type,
						 Predicate<BlockState> matcher, int radius, boolean absolute, int wanted,
						 boolean tfcAssociateFallback)
	{
		this.source = source;
		this.level = level;
		this.type = type;
		this.radius = radius;
		this.absolute = absolute;
		this.wanted = Math.max(1, wanted);
		this.matcher = matcher;
		this.tfcAssociateFallback = tfcAssociateFallback;
		this.fullScan = absolute||tfcAssociateFallback;

		ServerPlayer player = source.getPlayer();
		this.origin = player!=null?player.chunkPosition(): new ChunkPos(BlockPos.containing(source.getPosition()));
		this.searchedFrom = source.getPosition();

		if(fullScan)
		{
			// The old manual scan
			this.prediction = null;
			this.scanMinY = level.getMinBuildHeight();
			this.scanMaxY = level.getMaxBuildHeight()-1;
			this.queue = radiusQueue(origin, radius);
			this.stage = Stage.SCANNING;
		}
		else
		{
			this.prediction = type!=null
					?IGVeinLocator.predictAsync(level, (IWorldGenConfig)type, origin, radius)
					: IGVeinLocator.predictAnyAsync(level, origin, radius);
			this.stage = Stage.PREDICTING;
		}

		if(tfcAssociateFallback)
		{
			source.sendSuccess(() -> Component.translatable("command.immersivegeology.veinlocate.tfc_associate",
					targetName()).withStyle(ChatFormatting.GRAY), false);
		}
		source.sendSuccess(() -> Component.translatable(fullScan
				?"command.immersivegeology.veinlocate.searching"
				: "command.immersivegeology.veinlocate.predicting", targetName()), false);
	}

	public boolean isComplete()
	{
		return stage==Stage.DONE;
	}

	public boolean tick()
	{
		ServerPlayer player = source.getPlayer();
		if(player==null||player.hasDisconnected())
		{
			abandon();
			return true;
		}

		ticks++;
		switch(stage)
		{
			case PREDICTING -> tickPredicting();
			case SCANNING -> tickScanning();
			case DONE ->
			{
			}
		}
		return stage==Stage.DONE;
	}

	private void tickPredicting()
	{
		if(!prediction.isDone())
		{
			if(ticks%PROGRESS_INTERVAL==1)
			{
				showProgress(Component.translatable("command.immersivegeology.veinlocate.progress.seeds", radius));
			}
			return;
		}

		predicted = prediction.join();
		if(predicted.isEmpty())
		{
			finishEmpty();
			return;
		}

		scanMinY = predicted.minY();
		scanMaxY = predicted.maxY();
		queue = predicted.candidates();
		for(ChunkPos candidate : queue) candidateChunks.add(candidate.toLong());
		stage = Stage.SCANNING;
	}

	private void tickScanning()
	{
		ServerChunkCache chunkSource = level.getChunkSource();
		long deadline = System.nanoTime()+SCAN_BUDGET_NANOS;
		boolean progressed = false;

		requestChunks(chunkSource);

		Iterator<ChunkPos> iterator = inFlight.iterator();
		while(iterator.hasNext())
		{
			ChunkPos position = iterator.next();
			LevelChunk chunk = chunkSource.getChunkNow(position.x, position.z);
			// Still being generated on a worker thread - come back to it later
			if(chunk==null) continue;

			iterator.remove();
			chunkSource.removeRegionTicket(SCAN_TICKET, position, 0, position);
			chunksScanned++;
			progressed = true;

			List<OreHit> hits = IGVeinLocator.locateOreCentres(chunk, matcher, scanMinY, scanMaxY);
			if(hits.isEmpty())
			{
				queueRing(position);
			}
			for(OreHit hit : hits)
			{
				Found found = new Found(hit.position(), hit.material()!=null?hit.material(): type);
				if(isDistinctVein(found)) results.add(found);
			}

			if(results.size() >= wanted) stopRequesting = true;

			if(System.nanoTime() >= deadline) break;
		}

		stalledTicks = progressed||inFlight.isEmpty()?0: stalledTicks+1;
		if(stalledTicks >= STALL_LIMIT)
		{
			stalled = true;
			finish();
			return;
		}

		if(inFlight.isEmpty()&&(stopRequesting||hitChunkLimit||(priority.isEmpty()&&cursor >= queue.size())))
		{
			finish();
			return;
		}

		if(ticks%PROGRESS_INTERVAL==0)
		{
			int total = Math.min(CHUNK_LIMIT, Math.max(queue.size(), requested.size()));
			showProgress(Component.translatable("command.immersivegeology.veinlocate.progress.scan",
					chunksScanned, total));
		}
	}

	private void requestChunks(ServerChunkCache chunkSource)
	{
		for(ChunkPos position : inFlight)
		{
			chunkSource.addRegionTicket(SCAN_TICKET, position, 0, position);
		}

		if(stopRequesting) return;
		while(inFlight.size() < PREFETCH_DEPTH)
		{
			if(chunksScanned+inFlight.size() >= CHUNK_LIMIT)
			{
				hitChunkLimit = true;
				return;
			}
			ChunkPos position = nextChunk();
			if(position==null) return;
			inFlight.add(position);
			chunkSource.addRegionTicket(SCAN_TICKET, position, 0, position);
		}
	}

	@Nullable
	private ChunkPos nextChunk()
	{
		while(!priority.isEmpty())
		{
			ChunkPos position = priority.poll();
			if(requested.add(position.toLong())) return position;
		}
		while(cursor < queue.size())
		{
			ChunkPos position = queue.get(cursor++);
			if(requested.add(position.toLong())) return position;
		}
		return null;
	}

	/**
	 * A vein writes into the ring around the chunk it was rolled for, so when that chunk holds no ore the vein may
	 * still in a chunk over. So we add them to the front of the search as it's still a high chance candidate
	 */
	private void queueRing(ChunkPos barren)
	{
		if(fullScan||!candidateChunks.contains(barren.toLong())) return;

		int spread = IGVeinLocator.VEIN_CHUNK_SPREAD;
		for(int x = -spread; x <= spread; x++)
		{
			for(int z = -spread; z <= spread; z++)
			{
				ChunkPos neighbour = new ChunkPos(barren.x+x, barren.z+z);
				if(!requested.contains(neighbour.toLong())) priority.add(neighbour);
			}
		}
	}

	/** Every chunk in the radius, nearest first. */
	private static List<ChunkPos> radiusQueue(ChunkPos origin, int radius)
	{
		int span = radius*2+1;
		List<ChunkPos> queue = new ArrayList<>(span*span);
		for(int x = -radius; x <= radius; x++)
		{
			for(int z = -radius; z <= radius; z++)
			{
				queue.add(new ChunkPos(origin.x+x, origin.z+z));
			}
		}
		queue.sort(Comparator.comparingLong(chunk -> IGVeinLocator.distanceSq(origin, chunk)));
		return queue;
	}

	/**
	 * Enusre that veins don't misreport if it spills into chunks nearby
	 */
	private boolean isDistinctVein(Found candidate)
	{
		for(Found found : results)
		{
			if(found.material()!=candidate.material()) continue;
			if(found.position().distSqr(candidate.position()) < (double)DISTINCT_VEIN_DISTANCE*DISTINCT_VEIN_DISTANCE)
			{
				return false;
			}
		}
		return true;
	}

	/** A confirmed deposit :) */
	private record Found(BlockPos position, @Nullable MaterialInterface<?> material)
	{
		Component name()
		{
			return material!=null?material.getTranslation()
					: Component.translatable("command.immersivegeology.veinlocate.any");
		}
	}

	private Component targetName()
	{
		return type!=null?type.getTranslation()
				: Component.translatable("command.immersivegeology.veinlocate.any");
	}

	private void finish()
	{
		releaseTickets();
		stage = Stage.DONE;
		if(results.isEmpty())
		{
			finishEmpty();
			return;
		}

		// assume the player wants the closest ore, it can arrive out of order so we need to sort it.
		results.sort(Comparator.comparingDouble(found -> distanceSqTo(found.position())));
		for(Found found : results.subList(0, Math.min(wanted, results.size())))
		{
			BlockPos result = found.position();
			int distance = Mth.floor(Mth.sqrt((float)distanceSqTo(result)));
			Component coordinates = ComponentUtils.wrapInSquareBrackets(
					Component.translatable("chat.coordinates", result.getX(), result.getY(), result.getZ())
			).withStyle(style -> style.withColor(ChatFormatting.GREEN)
					.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
							"/tp @s "+result.getX()+" "+result.getY()+" "+result.getZ()))
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
			);
			source.sendSuccess(() -> Component.translatable("command.immersivegeology.veinlocate",
					found.name(), coordinates, distance), false);
		}

		if(hitChunkLimit)
		{
			source.sendSuccess(() -> Component.translatable("command.immersivegeology.veinlocate.limit", CHUNK_LIMIT)
					.withStyle(ChatFormatting.GRAY), false);
		}
	}

	private double distanceSqTo(BlockPos position)
	{
		return searchedFrom.distanceToSqr(position.getX(), position.getY(), position.getZ());
	}

	private void finishEmpty()
	{
		releaseTickets();
		stage = Stage.DONE;
		if(stalled)
		{
			source.sendFailure(Component.translatable("command.immersivegeology.veinlocate.stalled"));
			return;
		}
		if(hitChunkLimit)
		{
			source.sendFailure(Component.translatable("command.immersivegeology.veinlocate.limit", CHUNK_LIMIT));
			return;
		}

		Outcome outcome = predicted!=null?predicted.outcome(): Outcome.NOTHING_ROLLED;
		switch(outcome)
		{
			case DISABLED -> source.sendFailure(Component.translatable(
					"command.immersivegeology.veinlocate.disabled", targetName()));
			case WRONG_DIMENSION -> source.sendFailure(Component.translatable(
					"command.immersivegeology.veinlocate.wrong_dimension",
					targetName(), level.dimension().location().toString()));
			// The seed rolled veins here but no ore survived in the ground, so the stone in range refused it.
			case FOUND -> source.sendFailure(Component.translatable(
					"command.immersivegeology.veinlocate.unplaced", targetName()));
			default -> source.sendFailure(Component.translatable(
					"command.immersivegeology.veinlocate.none", targetName(), radius));
		}
	}

	/** Abandons the search without reporting - the player it was for is gone. */
	private void abandon()
	{
		releaseTickets();
		stage = Stage.DONE;
	}

	private void releaseTickets()
	{
		ServerChunkCache chunkSource = level.getChunkSource();
		for(ChunkPos position : inFlight)
		{
			chunkSource.removeRegionTicket(SCAN_TICKET, position, 0, position);
		}
		inFlight.clear();
	}

	private void showProgress(Component message)
	{
		ServerPlayer player = source.getPlayer();
		if(player!=null) player.displayClientMessage(message.copy().withStyle(ChatFormatting.YELLOW), true);
	}
}
