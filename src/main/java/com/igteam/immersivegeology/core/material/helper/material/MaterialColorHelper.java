/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.common.block.IGOreBlock.MineralWeathering;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class MaterialColorHelper
{
	public static Function<Integer, Integer> setupWeatheredColors(List<Pair<MineralWeathering, Integer>> oxidation_color)
	{
		List<Pair<MineralWeathering, Integer>> modifiableList = new ArrayList<>(oxidation_color);
		modifiableList.sort(Comparator.comparingInt(pair -> pair.getFirst().ordinal()));
		return (i) -> oxidation_color.get(i).getSecond();
	}

	public static Pair<MineralWeathering, Integer> weatheredColor(MineralWeathering oxidation, Integer color)
	{
		return Pair.of(oxidation, color);
	}
}
