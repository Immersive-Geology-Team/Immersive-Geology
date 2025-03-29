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
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class IGMineralTestingItem extends IGGenericItem
{
	private final HashMap<ChunkPos, MineralCacheEntry> cached_test = new HashMap<>();
	public IGMineralTestingItem(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material, new Properties().durability(128));
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
		Level level = context.getLevel();
		BlockPos usedPos = context.getClickedPos();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if(!stack.getItem().equals(this)) return InteractionResult.FAIL;
		if(player == null) return InteractionResult.FAIL;
		ChunkAccess centreChunk = level.getChunk(usedPos);
		boolean hasCache = cached_test.containsKey(centreChunk.getPos());
		if(hasCache)
		{
			MineralCacheEntry cached_entry = cached_test.get(centreChunk.getPos());
			long current_timestamp = System.currentTimeMillis();
			if((current_timestamp - cached_entry.timestamp) > MineralCacheEntry.CACHE_EXPIRY)
			{
				cached_test.clear();
			}

			player.displayClientMessage(Component.literal(cached_entry.message), true);
			return InteractionResult.SUCCESS;
		}

		Map<MaterialInterface<?>, Integer> queryMap = new HashMap<>();
		BlockPos chunkWorldPosition = centreChunk.getPos().getWorldPosition();
		int height = centreChunk.getHeight();
		for(int y = 0; y < height; ++y)
		{
			for(int x = -16; x < 32; ++x)
			{
				for(int z = -16; z < 32; ++z)
				{
					BlockPos cursor = new BlockPos(chunkWorldPosition).offset(x, y, z);
					BlockState check = level.getBlockState(cursor);
					if (check.getBlock() instanceof IOreBlock ore) {
						MaterialInterface<?> material = ore.getMaterial(MaterialTexture.overlay);
						queryMap.merge(material, 1, Integer::sum);
					} else {
						level.removeBlock(cursor,false);
					}
				}
			}
		}
		List<MaterialInterface<?>> found = queryMap.entrySet().stream()
				.sorted(Map.Entry.<MaterialInterface<?>, Integer>comparingByValue().reversed())
				.limit(3)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		String string_found = "";
		if(!found.isEmpty())
		{
			if(found.size() == 1)
			{
				string_found = "Found Traces of " + found.get(0);
			}
			if(found.size() == 2)
			{
				string_found = "Found Traces of "+ found.get(0) +" and " + found.get(1) ;
			}
			if(found.size() == 3)
			{
				string_found = "Found Cluster of "+ found.get(0) +", "+ found.get(1) +" and "+ found.get(2);
			}
		}

		String status = found.isEmpty() ? "nothing" : "found";
		Component comp = Component.translatable("immersivegeology.prospecting_pick." + status, string_found);
		player.displayClientMessage(comp, true);
		stack.hurtAndBreak(1, player, (p) -> {
		});
		cached_test.put(centreChunk.getPos(), new MineralCacheEntry(comp.getString()));
		return InteractionResult.SUCCESS;
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
