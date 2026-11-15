/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache Resolver for ore blocks allows lookups for every (material, stone, richness) combination.
 * <p>
 * The uncached lookup would cost about six string allocations per call... on a loop over {@link
 * com.igteam.immersivegeology.core.material.helper.flags.ModFlags} to work out the registry prefix, three
 * lowercase conversions and the surrounding concatenations, before it hits the registry map lookup. World
 * generation calls it once per candidate block... That's a shitload of calls.
 * <p>
 * A material's whole table is filled on first use and never changes afterwards. Blocks are registry singletons and
 * the combinations that exist are decided during registration. Combinations that were never registered stay null,
 * so we can just cache everything instead, it's not a huge performance boost... about like ~5% better in the best case,
 * around ~2% in the worst.
 *
 * Performance was most affected by changes are in how the noise generates.
 */
public final class IGOreBlockIndex
{
	private IGOreBlockIndex()
	{
	}

	private static final Map<MaterialHelper, IOreBlock[][]> INDEX = new ConcurrentHashMap<>();

	/**
	 * The ore block for this combination, or null when no such block was registered.
	 */
	public static IOreBlock get(MaterialHelper ore, IStoneType stone, OreRichness richness)
	{
		IOreBlock[][] table = INDEX.get(ore);
		if(table==null)
		{
			table = INDEX.computeIfAbsent(ore, IGOreBlockIndex::build);
		}
		return table[stone.index()][richness.ordinal()];
	}

	private static IOreBlock[][] build(MaterialHelper ore)
	{
		OreRichness[] grades = OreRichness.values();
		IOreBlock[][] table = new IOreBlock[IGStoneTypes.count()][grades.length];

		for(IStoneType stone : IGStoneTypes.all())
		{
			for(OreRichness richness : grades)
			{
				table[stone.index()][richness.ordinal()] = resolve(ore, stone, richness);
			}
		}

		return table;
	}

	private static IOreBlock resolve(MaterialHelper ore, IStoneType stone, OreRichness richness)
	{
		try
		{
			return (IOreBlock)IGRegistrationHolder.getBlock.apply(
					BlockCategoryFlags.ORE_BLOCK.getRegistryKey(ore, stone, richness));
		} catch(Exception exception)
		{
			// Most combinations are not registered, often the material does not accept the stone's formation,
			// or the stone belongs to a mod that is not loaded. Not something worth logging thousands of times.
			return null;
		}
	}
}
