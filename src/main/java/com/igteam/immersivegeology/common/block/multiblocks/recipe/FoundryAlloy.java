/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;

import java.util.List;
import java.util.Locale;

public enum FoundryAlloy
{
	BRONZE(MetalEnum.Bronze, 4, 60, new Part(MetalEnum.Copper, 3), new Part(MetalEnum.Tin, 1)),
	STAINLESS_STEEL(MetalEnum.StainlessSteel, 8, 120, new Part(MetalEnum.Steel, 6), new Part(MetalEnum.Chromium, 2)),
	HASTELLOY(MetalEnum.Hastelloy, 12, 200,
			new Part(MetalEnum.Nickel, 8), new Part(MetalEnum.Chromium, 4), new Part(MetalEnum.Molybdenum, 2),
			new Part(MetalEnum.Iron, 1), new Part(MetalEnum.Tungsten, 1));

	public static final int ENERGY_PER_PART = 4;

	private final MetalEnum result;
	private final int resultParts;
	private final int time;
	private final List<Part> parts;

	FoundryAlloy(MetalEnum result, int resultParts, int time, Part... parts)
	{
		this.result = result;
		this.resultParts = resultParts;
		this.time = time;
		this.parts = List.of(parts);
	}

	public MetalEnum getResult()
	{
		return result;
	}

	public int getResultParts()
	{
		return resultParts;
	}

	public int getTime()
	{
		return time;
	}

	public int getEnergyPerUnit()
	{
		return resultParts*ENERGY_PER_PART;
	}

	public List<Part> getParts()
	{
		return parts;
	}

	public boolean isCastable()
	{
		if(!result.hasFlag(BlockCategoryFlags.FLUID)) return false;
		for(Part part : parts)
			if(!part.metal().hasFlag(BlockCategoryFlags.FLUID)) return false;
		return true;
	}

	public String getRecipeName()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	public record Part(MetalEnum metal, int parts)
	{
	}
}
