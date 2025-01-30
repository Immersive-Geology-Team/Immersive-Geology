/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class IGWorldSubscription
{
	private static final List<Block> removeListIron = List.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK);
	private static final List<Block> removeListCopper = List.of(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK);

	private static Boolean removeIron = null;
	private static Boolean removeCopper = null;
	private static Ores oreConfigs = null;


	public static void getConfigValues()
	{
		removeIron = IGServerConfig.REMOVAL.shouldRemoveIron.get();
		removeCopper = IGServerConfig.REMOVAL.shouldRemoveCopper.get();
		oreConfigs = IGServerConfig.ORES;
	}

	// This is somewhat of a dirty method to remove the ore veins, I think it may be possible to set their size to zero
	// But I've had a lot of trouble finding information on this.
		
	@SubscribeEvent
	public void forceRemoveVanillaVeins(ChunkEvent.Load event)
	{
		if(removeCopper==null||removeIron==null||oreConfigs==null) getConfigValues();
		ChunkAccess access = event.getChunk();
		LevelAccessor level = event.getLevel();

		if(event.isNewChunk())
		{
			if(!removeIron&&!removeCopper) return;
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			for(int i = access.getMinSection(); i < access.getMaxSection(); ++i)
			{
				LevelChunkSection levelchunksection = access.getSection(access.getSectionIndexFromSectionY(i));
				BlockPos blockpos = SectionPos.of(access.getPos(), i).origin();
				for(int j = 0; j < 16; ++j)
				{
					for(int k = 0; k < 16; ++k)
					{
						for(int l = 0; l < 16; ++l)
						{
							BlockState blockstate = levelchunksection.getBlockState(l, j, k);
							cursor.setWithOffset(blockpos, l, j, k);
							if((removeIron && removeListIron.contains(blockstate.getBlock())||(removeCopper && removeListCopper.contains(blockstate.getBlock()))))
							{
								event.getChunk().setBlockState(cursor, j > 0?Blocks.STONE.defaultBlockState(): Blocks.DEEPSLATE.defaultBlockState(), true);
							}
						}
					}
				}
			}
		}

	}
}
