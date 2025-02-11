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
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class IGWorldSubscription
{

	private static final BlockState stoneState = Blocks.STONE.defaultBlockState();
	private static final BlockState deepslateState = Blocks.DEEPSLATE.defaultBlockState();
	private static final BlockState netherStone = Blocks.NETHERRACK.defaultBlockState();

	private static final List<Block> REMOVE_LIST_IRON = List.of(
			Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK);
	private static final List<Block> REMOVE_LIST_COPPER = List.of(
			Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK);

	private static final List<Block> REMOVE_LIST_GOLD = List.of(
			Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.RAW_GOLD_BLOCK, Blocks.NETHER_GOLD_ORE);

	// List of maps for IE metal ores
	private static final List<Map<EnumMetals, BlockEntry<Block>>> IE_ORE_METALS =
			List.of(Metals.ORES, Metals.DEEPSLATE_ORES, Metals.RAW_ORES);

	// Config flags (loaded on first chunk event)
	private static Boolean removeIron = null;
	private static Boolean removeGold = null;
	private static Boolean removeCopper = null;
	private static Boolean removeIEBauxite = null;
	private static Boolean removeIELead = null;
	private static Boolean removeIESilver = null;
	private static Boolean removeIEUranium = null;
	private static Boolean removeIENickel = null;
	private static Ores oreConfigs = null;

	// Set of blocks to remove (built from the above flags)
	private static final Set<Block> removalBlocks = new HashSet<>();

	/**
	 * Loads the configuration values and precomputes the set of blocks to remove.
	 */
	public static void getConfigValues() {
		removeIron = IGServerConfig.REMOVAL.shouldRemoveIron.get();
		removeCopper = IGServerConfig.REMOVAL.shouldRemoveCopper.get();
		removeGold = IGServerConfig.REMOVAL.shouldRemoveGold.get();

		removeIEBauxite = IGServerConfig.REMOVAL.shouldRemoveIEBauxite.get();
		removeIELead = IGServerConfig.REMOVAL.shouldRemoveIELead.get();
		removeIESilver = IGServerConfig.REMOVAL.shouldRemoveIESilver.get();
		removeIEUranium = IGServerConfig.REMOVAL.shouldRemoveIEUranium.get();
		removeIENickel = IGServerConfig.REMOVAL.shouldRemoveIENickel.get();
		oreConfigs = IGServerConfig.ORES;

		// Rebuild the removal set based on the config flags.
		removalBlocks.clear();
		if (Boolean.TRUE.equals(removeIron)) {
			removalBlocks.addAll(REMOVE_LIST_IRON);
		}
		if (Boolean.TRUE.equals(removeCopper)) {
			removalBlocks.addAll(REMOVE_LIST_COPPER);
		}
		if(Boolean.TRUE.equals(removeGold))
		{
			removalBlocks.addAll(REMOVE_LIST_GOLD);
		}
		if (Boolean.TRUE.equals(removeIEUranium)) {
			for (Map<EnumMetals, BlockEntry<Block>> map : IE_ORE_METALS) {
				removalBlocks.add(map.get(EnumMetals.URANIUM).get());
			}
		}
		if (Boolean.TRUE.equals(removeIEBauxite)) {
			for (Map<EnumMetals, BlockEntry<Block>> map : IE_ORE_METALS) {
				removalBlocks.add(map.get(EnumMetals.ALUMINUM).get());
			}
		}
		if (Boolean.TRUE.equals(removeIELead)) {
			for (Map<EnumMetals, BlockEntry<Block>> map : IE_ORE_METALS) {
				removalBlocks.add(map.get(EnumMetals.LEAD).get());
			}
		}
		if (Boolean.TRUE.equals(removeIENickel)) {
			for (Map<EnumMetals, BlockEntry<Block>> map : IE_ORE_METALS) {
				removalBlocks.add(map.get(EnumMetals.NICKEL).get());
			}
		}
		if (Boolean.TRUE.equals(removeIESilver)) {
			for (Map<EnumMetals, BlockEntry<Block>> map : IE_ORE_METALS) {
				removalBlocks.add(map.get(EnumMetals.SILVER).get());
			}
		}
	}

	@SubscribeEvent
	public void forceRemoveVanillaVeins(ChunkEvent.Load event)
	{
		if(!event.isNewChunk()) return;
		boolean isNether = false;
		// Ensure config values are loaded.
		if(removeCopper==null||removeIron==null||oreConfigs==null)
		{
			getConfigValues();
		}

		// If no removal flags are set, exit early.
		if(!(Boolean.TRUE.equals(removeIron)||
				Boolean.TRUE.equals(removeCopper)||
				Boolean.TRUE.equals(removeIEUranium)||
				Boolean.TRUE.equals(removeIEBauxite)||
				Boolean.TRUE.equals(removeIELead)||
				Boolean.TRUE.equals(removeIENickel)||
				Boolean.TRUE.equals(removeIESilver)))
		{
			return;
		}
		LevelAccessor level = event.getLevel();

		if(level instanceof ServerLevel slevel)
		{
			isNether = slevel.dimension().equals(Level.NETHER);
		}

		ChunkAccess chunk = event.getChunk();
		if(event.isNewChunk())
		{
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			// Loop over all sections in the chunk.
			for(int sectionY = chunk.getMinSection(); sectionY < chunk.getMaxSection(); ++sectionY)
			{
				LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
				if(section==null)
				{
					continue;
				}
				BlockPos sectionOrigin = SectionPos.of(chunk.getPos(), sectionY).origin();
				// Iterate over all 16x16x16 block positions in this section.
				for(int y = 0; y < 16; y++)
				{
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							BlockState currentState = section.getBlockState(x, y, z);
							if(removalBlocks.contains(currentState.getBlock()))
							{
								cursor.setWithOffset(sectionOrigin, x, y, z);
								// Use deepslate for the bottom layer (y == 0), stone otherwise.
								chunk.setBlockState(cursor, (isNether ? netherStone : (y > 0?stoneState: deepslateState)), true);
							}
						}
					}
				}
			}
		}
	}
}
