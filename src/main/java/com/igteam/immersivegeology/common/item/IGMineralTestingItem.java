/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class IGMineralTestingItem extends IGGenericItem implements IGFlagItem
{
	private final HashMap<ChunkPos, MineralCacheEntry> cached_test = new HashMap<>();

	public IGMineralTestingItem(ItemCategoryFlags flag, MaterialInterface<?> material, int durability)
	{
		super(flag, material, new Properties().durability(durability));
	}

	@Override
	public boolean isDamageable(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getMaxStackSize(ItemStack stack)
	{
		return 1;
	}

	@Override
	public @NotNull Component getName(ItemStack stack)
	{
		return Component.translatable("item.immersivegeology.prospector_pick");
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player == null || !stack.getItem().equals(this)) return InteractionResult.FAIL;
		Level level = context.getLevel();
		BlockPos usedPos = context.getClickedPos();
		ChunkPos centreChunkPos = new ChunkPos(usedPos);

		MineralCacheEntry cachedEntry = cached_test.get(centreChunkPos);
		if (cachedEntry != null) {
			long currentTimestamp = System.currentTimeMillis();
			if ((currentTimestamp - cachedEntry.timestamp) > MineralCacheEntry.CACHE_EXPIRY) {
				cached_test.clear();
			} else {
				player.displayClientMessage(Component.literal(cachedEntry.message), true);
				return InteractionResult.SUCCESS;
			}
		}

		long startTime = System.nanoTime();
		int centreChunkX = centreChunkPos.x;
		int centreChunkZ = centreChunkPos.z;
		int minBuildHeight = level.getMinBuildHeight();
		int maxBuildHeight = level.getMaxBuildHeight();
		int sectionMin = level.getSectionIndex(minBuildHeight);
		int sectionMax = level.getSectionIndex(maxBuildHeight);

		Set<MaterialInterface<?>> oreSet = new HashSet<>();
		TagKey<Block> allOresTag = BlockCategoryFlags.ORE_BLOCK.getCategoryTag();
		chunkScan: for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				ChunkAccess chunk = level.getChunk(centreChunkX + dx, centreChunkZ + dz);

				// Scan through sections first
				for (int sectionIndex = sectionMin; sectionIndex < sectionMax; sectionIndex++) {
					LevelChunkSection section = chunk.getSection(sectionIndex);

					// Skip empty sections
					if (section.hasOnlyAir()) continue;

					// Broad check - if the section doesn't have any ores at all, skip it entirely
					if (!section.maybeHas(b -> b.is(allOresTag))) continue;

					for (int x = 0; x < 16; x++)
					{
						for(int y = 0; y < 16; y++)
						{
							for(int z = 0; z < 16; z++)
							{
								BlockState blockState = section.getBlockState(x, y, z);
								if(blockState.is(allOresTag))
								{
									IOreBlock ore = (IOreBlock) blockState.getBlock();
									oreSet.add(ore.getOreMaterial());
									if (oreSet.size() >= 3) break chunkScan;
								}
							}
						}
					}
				}
			}
		}

		long endTime = System.nanoTime();
		double milliseconds = (endTime - startTime) / 1_000_000.0;

		// Print scan results and performance metrics
		System.out.println("--- Ore Scan Performance ---");
		System.out.println("Sections scanned: " + (sectionMax - sectionMin));
		System.out.println("Ores found: " + oreSet.size() + " [" + String.join(", ", oreSet.stream().map(MaterialInterface::getName).toList()) + "]");
		System.out.println("Execution time: " + String.format("%.2f", milliseconds) + " ms");
		System.out.println("--------------------------");
		Component message = getMessage(oreSet);

		// Update cache and display message
		player.displayClientMessage(message, true);
		cached_test.replace(centreChunkPos, new MineralCacheEntry(message.getString()));

		stack.hurtAndBreak(1, player, (p) -> {});
		return InteractionResult.SUCCESS;
	}

	private static @NotNull Component getMessage(Set<MaterialInterface<?>> oreSet)
	{
		Component message;
		if (oreSet.isEmpty()) {
			message = Component.translatable("immersivegeology.prospecting_pick.nothing");
		} else {
			List<MaterialInterface<?>> found = new ArrayList<>(oreSet);
			String messageKey = "immersivegeology.prospecting_pick.found";
			String materialsText;

			switch (found.size()) {
				case 1:
					materialsText = "Found Traces of " + found.get(0);
					break;
				case 2:
					materialsText = "Found Traces of " + found.get(0) + " and " + found.get(1);
					break;
				default: // 3 or more
					materialsText = "Found Cluster of " + found.get(0) + ", " + found.get(1) + " and " + found.get(2);
					break;
			}

			message = Component.translatable(messageKey, materialsText);
		}
		return message;
	}

	private static class MineralCacheEntry {
		public static final long CACHE_EXPIRY = 10 * 1000;
		final String message;
		final long timestamp;

		public MineralCacheEntry(String message) {
			this.message = message;
			this.timestamp = System.currentTimeMillis();
		}
	}
}
