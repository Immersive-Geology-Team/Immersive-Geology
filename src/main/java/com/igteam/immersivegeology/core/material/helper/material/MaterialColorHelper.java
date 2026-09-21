package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class MaterialColorHelper
{
	private MaterialColorHelper()
	{
	}

	public static class WeatheredColor
	{
		private final MineralWeathering stage;
		private final int color;

		WeatheredColor(MineralWeathering stage, int color)
		{
			this.stage = stage;
			this.color = color;
		}

		public MineralWeathering stage()
		{
			return stage;
		}

		public int color()
		{
			return color;
		}
	}

	public static WeatheredColor weatheredColor(MineralWeathering stage, Integer color)
	{
		return new WeatheredColor(stage, color);
	}

	public static Function<Integer, Integer> setupWeatheredColors(List<WeatheredColor> colors)
	{
		List<WeatheredColor> sorted = new ArrayList<>(colors);
		sorted.sort(Comparator.comparingInt(c -> c.stage().ordinal()));

		int stages = MineralWeathering.values().length;
		int[] table = new int[stages];
		for(int i = 0; i < stages; i++)
			table[i] = sorted.get(Math.min(i, sorted.size()-1)).color();

		return index -> table[index==null||index < 0?0: Math.min(index, table.length-1)];
	}

	public static Function<Integer, Integer> setupWeatheredColors(WeatheredColor... colors)
	{
		return setupWeatheredColors(Arrays.asList(colors));
	}
}
