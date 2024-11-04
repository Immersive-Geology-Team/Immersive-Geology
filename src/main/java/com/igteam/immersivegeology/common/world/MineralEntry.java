/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.mojang.serialization.Codec;

import java.util.ArrayList;

public class MineralEntry
{
	MineralEnum mineral;
	StoneEnum stone;
	OreRichness richness;

	public MineralEntry(MineralEnum mineral, StoneEnum stone, OreRichness richness)
	{
		this.mineral = mineral;
		this.stone = stone;
		this.richness = richness;
	}

	public static final ArrayList<MineralEntry> VALUES = values();
	public static final Codec<MineralEntry> CODEC = Codec.INT.xmap(
			VALUES::get,
			VALUES::indexOf
	);

	public static ArrayList<MineralEntry> values()
	{
		MineralEnum[] minerals = MineralEnum.values();
		StoneEnum[] stones = StoneEnum.values();
		OreRichness[] richnessValues = OreRichness.values();

		ArrayList<MineralEntry> entries = new ArrayList<>();

		for(StoneEnum stone : stones)
		{
			for(MineralEnum mineral : minerals)
			{
				if(mineral.instance().acceptableStoneType(stone.instance()))
				{
					for(OreRichness richness : richnessValues)
					{
						MineralEntry entry = new MineralEntry(mineral, stone, richness);
						if(entries.stream().anyMatch(e -> e.getName().equals(entry.getName()))) continue;
						entries.add(entry);
					}
				}
			}
		}

		return entries;
	}

	public String getName()
	{
		return this.mineral.getName().toLowerCase() + "_" + this.stone.getName().toLowerCase() + "_" + this.richness.name().toLowerCase();
	}

	public MineralEnum getMineral()
	{
		return mineral;
	}

	public OreRichness getRichness()
	{
		return richness;
	}

	public StoneEnum getStone()
	{
		return stone;
	}
}
