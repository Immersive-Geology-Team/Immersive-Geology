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
import com.mojang.datafixers.kinds.CartesianLike;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public record MineralCombination(List<MineralEnum> minerals, List<StoneEnum> stones) {

	public static final Codec<MineralCombination> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MineralEnum.CODEC.listOf().fieldOf("minerals").forGetter(MineralCombination::minerals),
			StoneEnum.CODEC.listOf().fieldOf("stones").forGetter(MineralCombination::stones)
	).apply(instance, MineralCombination::new));

	public Iterable<MineralEntry> entries()
	{
		List<MineralEntry> entries = new ArrayList<>();
		for(StoneEnum stone : stones)
		{
			for(MineralEnum mineral : minerals)
			{
				if(mineral.instance().acceptableStoneType(stone.instance())) entries.add(new MineralEntry(mineral, stone));
			}
		}
		return entries;
	}
}
