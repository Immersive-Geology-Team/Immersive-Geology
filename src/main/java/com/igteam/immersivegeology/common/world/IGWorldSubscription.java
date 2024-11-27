/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber
public class IGWorldSubscription
{
	private static final List<Block> removeList = List.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK);
	private static final Predicate<Block> remove = removeList::contains;

	@SubscribeEvent
	public void forceRemoveVanillaVeins(ChunkEvent event)
	{
		LevelAccessor level = event.getLevel();
		ChunkPos pos = event.getChunk().getPos();
		int maxZ = pos.getMaxBlockZ();
		int maxX = pos.getMaxBlockX();
		int minZ = pos.getMinBlockZ();
		int minX = pos.getMinBlockX();

		int minY = level.getMinBuildHeight();
		int maxY = level.getMaxBuildHeight();

		for(int x = minX; x < maxX; x++)
		{
			for(int z = minZ; z < maxZ; z++)
			{
				for(int y = minY; y < maxY; y++)
				{
					BlockPos bpos = new BlockPos(x,y,z);
					BlockState block = level.getBlockState(bpos);
					if(remove.test(block.getBlock()))
					{
						level.setBlock(bpos, y > 0 ? Blocks.STONE.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState(), 3);
					}
				}
			}
		}

	}
}
