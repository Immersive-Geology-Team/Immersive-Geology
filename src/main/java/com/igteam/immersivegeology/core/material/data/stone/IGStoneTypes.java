/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone;

import net.minecraft.block.state.IBlockState;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class IGStoneTypes
{
	private IGStoneTypes()
	{
	}

	private static final List<IStoneType> REGISTERED;

	static
	{
		List<IStoneType> registered = new ArrayList<>(List.of(StoneEnum.values()));

		// Config-declared stone types are deferred to Phase 4 (world generation);
		// see deferred/phase4/stone-config.
		REGISTERED = List.copyOf(registered);

		int declared = REGISTERED.size()-StoneEnum.values().length;
		if(declared > 0) IGLib.IG_LOGGER.info("{} stone type(s) added from configuration", declared);
	}

	public static List<IStoneType> all()
	{
		return REGISTERED;
	}

	public static int count()
	{
		return REGISTERED.size();
	}

	public static void logDeclaredStoneSummary()
	{
		for(IStoneType stone : REGISTERED)
		{
			if(stone.getHostBlockId()==null) continue;

			StoneFormation formation = stone.instance().getStoneFormation();
			for(ResourceLocation dimension : stone.getDimensions())
			{
				List<String> hosts = new ArrayList<>();
				for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
				{
					if(!material.instance().isValidStoneFormation(formation)) continue;
					if(!material.instance().getAcceptableDimensions().contains(dimension.toString())) continue;
					if(stone.excludesOre(material.instance())) continue;
					hosts.add(material.getName());
				}

				if(hosts.isEmpty())
				{
					IGLib.IG_LOGGER.warn(
							"Declared stone {} in {}: no mineral will generate in it. Nothing accepts {} while also "+
									"being allowed in that dimension. Check the formation, or adjust the ore's "+
									"dimension whitelist in the server config.",
							stone.getHostBlockId(), dimension, formation);
				}
				else
				{
					IGLib.IG_LOGGER.info("Declared stone {} in {}: {} mineral(s) will generate in it ({})",
							stone.getHostBlockId(), dimension, hosts.size(), String.join(", ", hosts));
				}
			}
		}
	}

	private static final Map<Block, Optional<IStoneType>> worldStateCache = new ConcurrentHashMap<>();

	public static IStoneType fromWorldState(IBlockState state)
	{
		Block block = state.getBlock();
		Optional<IStoneType> cached = worldStateCache.get(block);
		if(cached==null)
		{
			cached = worldStateCache.computeIfAbsent(block, b -> Optional.ofNullable(resolve(b, state)));
		}
		return cached.orElse(null);
	}

	private static IStoneType resolve(Block block, IBlockState state)
	{
		IStoneType declared = declaredHosts().get(block.getRegistryName());
		if(declared!=null) return declared;

		return StoneEnum.selectWorldState(state);
	}

	private static volatile Map<ResourceLocation, IStoneType> declaredHosts;

	private static Map<ResourceLocation, IStoneType> declaredHosts()
	{
		Map<ResourceLocation, IStoneType> cached = declaredHosts;
		if(cached==null)
		{
			Map<ResourceLocation, IStoneType> built = new HashMap<>();
			for(IStoneType stone : REGISTERED)
			{
				ResourceLocation host = stone.getHostBlockId();
				if(host==null) continue;
				IStoneType clash = built.put(host, stone);
				if(clash!=null)
				{
					IGLib.IG_LOGGER.error("Stone types {} and {} both claim the block {}; keeping {}",
							clash.getName(), stone.getName(), host, stone.getName());
				}
			}
			cached = Map.copyOf(built);
			declaredHosts = cached;
		}
		return cached;
	}
}
