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
import com.igteam.immersivegeology.core.material.data.stone.config.ConfigStoneEntry;
import com.igteam.immersivegeology.core.material.data.stone.config.ConfigStoneType;
import com.igteam.immersivegeology.core.material.data.stone.config.IGStoneTypeConfig;
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

	/**
	 * Declaration order matters: it decides the order ore blocks are registered in, and therefore the order they
	 * appear in generated resources. The built-ins come first and keep the enum's own order, so these new declared
	 * rock types never disturbs the existing block ids.
	 */
	private static final List<IStoneType> REGISTERED;

	static
	{
		List<IStoneType> registered = new ArrayList<>(List.of(StoneEnum.values()));

		int index = registered.size();
		for(ConfigStoneEntry entry : IGStoneTypeConfig.load())
		{
			registered.add(new ConfigStoneType(entry, index++));
		}

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

	/**
	 * Reports, for every declared rock type, how many minerals will actually generate in it.
	 */
	public static void logDeclaredStoneSummary()
	{
		for(IStoneType stone : REGISTERED)
		{
			if(stone.getHostBlockId()==null) continue;

			StoneFormation formation = stone.instance().getStoneFormation();
			for(ResourceLocation dimension : stone.getDimensions())
			{
				List<String> hosts = new ArrayList<>();
				for(MaterialInterface<?> material : IGLib.getGeneratedMaterials())
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

	/**
	 * Resolved rock type per block, If it's empty, it's not a rock type Immersive Geology knows.
	 */
	private static final Map<Block, Optional<IStoneType>> worldStateCache = new ConcurrentHashMap<>();

	/** The rock type this world block is, or null when it is not one Immersive Geology knows. */
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
		IStoneType declared = declaredHosts().get(ForgeRegistries.BLOCKS.getKey(block));
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
