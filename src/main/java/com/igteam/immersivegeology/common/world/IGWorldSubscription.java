/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.BlockEntry;
import blusunrize.immersiveengineering.common.register.IEBlocks.Metals;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores;
import com.igteam.immersivegeology.common.world.features.helper.IGOreGenUtils;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IGWorldSubscription
{

	private static final BlockState stoneState = Blocks.STONE.defaultBlockState();
	private static final BlockState deepslateState = Blocks.DEEPSLATE.defaultBlockState();
	private static final BlockState netherStone = Blocks.NETHERRACK.defaultBlockState();

	private static final List<BlockState> REMOVE_LIST_IRON = List.of(
			Blocks.IRON_ORE.defaultBlockState(), Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState());
	private static final List<BlockState> REMOVE_LIST_COPPER = List.of(
			Blocks.COPPER_ORE.defaultBlockState(), Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState());

	private static final List<BlockState> REMOVE_LIST_GOLD = List.of(
			Blocks.GOLD_ORE.defaultBlockState(), Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), Blocks.RAW_GOLD_BLOCK.defaultBlockState(), Blocks.NETHER_GOLD_ORE.defaultBlockState());

	// Config flags (loaded on first chunk event)
	private static Boolean removeIron = false;
	private static Boolean removeGold = false;
	private static Boolean removeCopper = false;
	private static Ores oreConfigs = null;

	/**
	 * Loads the configuration values and precomputes the set of blocks to remove.
	 */
	public static void getConfigValues() {
		removeIron = IGServerConfig.REMOVAL.shouldRemoveIron.get();
		removeCopper = IGServerConfig.REMOVAL.shouldRemoveCopper.get();
		removeGold = IGServerConfig.REMOVAL.shouldRemoveGold.get();
		oreConfigs = IGServerConfig.ORES;
	}

	private final Predicate<BlockState> isReplaceable = (blockState)->(removeIron && REMOVE_LIST_IRON.contains(blockState)) || (removeCopper && REMOVE_LIST_COPPER.contains(blockState)) || removeGold && REMOVE_LIST_GOLD.contains(blockState);

	private static int chunksProcessed = 0;

	@SubscribeEvent
	public void levelLoad(LevelEvent.Load event)
	{
		chunksProcessed = 0;
		getConfigValues();
	}

	private Set<ResourceLocation> getBlacklistedBiomes()
	{
		return IGServerConfig.REMOVAL.biome_blacklist.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
	}

	// Okay, so, this event NEEDS to be optimized, as extra over head here, means slower chunk generation
	@SubscribeEvent
	public void forceRemoveVanillaVeins(ChunkEvent.Load event)
	{
		if(!event.isNewChunk()) return;
		long startTime = 0;
		boolean canLog = IGServerConfig.REMOVAL.logProcess.get();
		if(canLog) startTime = System.nanoTime();

		// Initialize Variables
		boolean isNether = false;
		boolean chunkModified = false;

		// Holder for the type of 'stone' to replace removed ore with
		BlockState replaceState = null;

		// Setup Accessors
		ChunkAccess chunk = event.getChunk();
		LevelAccessor level = event.getLevel();

		// If no removal flags are set, exit straight away
		if(!(removeIron||removeCopper||removeGold)) return;

		// Check if we're in the nether
		if(level instanceof ServerLevel slevel)
		{
			isNether = slevel.dimension().equals(Level.NETHER);
			replaceState = isNether ? netherStone : null;
		}
		Holder<Biome> holder = level.getBiome(chunk.getPos().getWorldPosition());
		if(holder.getTagKeys().anyMatch(((b) ->
		{
			if(getBlacklistedBiomes().contains(b.location()))
			{
				if(canLog)
				{
					IGLib.IG_LOGGER.info("Operation not permitted within {}", b.location());
					IGLib.IG_LOGGER.info("Change Server Configuration File if this is not desired");
				}
				return true;
			}
			return false;
		})))
		{
			return;
		}

		// Loop over all sections in the chunk.
		for(int sectionY = chunk.getMinSection(); sectionY < chunk.getMaxSection(); ++sectionY)
		{
			// Get the LevelChunkSection
			int sectionIndex = chunk.getSectionIndexFromSectionY(sectionY);
			LevelChunkSection section = chunk.getSection(sectionIndex);

			// Check for early exits asap
			// Is this section only air blocks?
			if(section.hasOnlyAir()) continue;

			// Does the section contain any blocks that we can replace?
			if(!section.maybeHas(isReplaceable)) continue;

			// We're now past early exits, so we know we're modifying this chunk
			chunkModified = true;

			// Now, we need to know if we're in the deepslate layer, or not
			// To optimize this we're checking the Min and Max positions for a section
			// This gives us about 0.01ms of extra performance per chunk.
			int minY = SectionPos.of(chunk.getPos(), sectionY).minBlockY();
			if(!isNether)
			{
				if((minY + 16) < 0) replaceState = deepslateState;
				if(minY > 0) replaceState = stoneState;
			}

			for(int y = 0; y < 16; y++)
			{
				// Seems like we're in a section that has blocks below and above 0, so we need to check the section in detail.
				if(replaceState == null)
				{
					int worldY = minY+y;
					replaceState = worldY < 0?deepslateState: stoneState;
				}

				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{
						BlockState currentState = section.getBlockState(x, y, z);
						boolean replace = isReplaceable.test(currentState);
						if(replace)
						{
							section.setBlockState(x,y,z, replaceState);
						}
					}
				}
			}
		}

		if(chunkModified)
		{
			chunk.setUnsaved(true);
		}

		if(canLog)
		{
			long endTime = System.nanoTime();
			long processingTime = endTime-startTime;
			chunksProcessed++;

			// Maintain a rolling list of the last 100 processing times
			if(last100ProcessingTimes.size() >= 100)
			{
				last100ProcessingTimes.pollFirst(); // Remove the oldest entry
			}
			last100ProcessingTimes.addLast(processingTime);

			// Log every 100 chunks
			if(chunksProcessed%100==0)
			{
				List<Long> sortedTimes = new ArrayList<>(last100ProcessingTimes);
				Collections.sort(sortedTimes);

				double medianTimeMs;
				int size = sortedTimes.size();
				if(size%2==0)
				{
					// Even number of elements: average the two middle values
					medianTimeMs = ((sortedTimes.get(size/2-1)+sortedTimes.get(size/2))/2.0)/1_000_000.0;
				}
				else
				{
					// Odd number of elements: take the middle value
					medianTimeMs = (sortedTimes.get(size/2)/1_000_000.0);
				}

				IGLib.IG_LOGGER.info("Processed {} chunks. Median processing time for last 100 chunks: {} ms per chunk",
						chunksProcessed, String.format("%.6f", medianTimeMs));
			}
		}
	}
	private final ArrayDeque<Long> last100ProcessingTimes = new ArrayDeque<>(100);
}
