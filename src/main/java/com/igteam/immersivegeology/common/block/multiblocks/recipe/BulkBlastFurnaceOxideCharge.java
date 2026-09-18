/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;

import java.util.Locale;

public enum BulkBlastFurnaceOxideCharge
{
	TIN(MetalEnum.Tin, 232),
	LEAD(MetalEnum.Lead, 327),
	ZINC(MetalEnum.Zinc, 420),
	COPPER(MetalEnum.Copper, 1085),
	MANGANESE(MetalEnum.Manganese, 1246),
	NICKEL(MetalEnum.Nickel, 1455),
	COBALT(MetalEnum.Cobalt, 1495),
	IRON(MetalEnum.Iron, 1538),
	CHROMIUM(MetalEnum.Chromium, 1907),
	MOLYBDENUM(MetalEnum.Molybdenum, 2623),
	TUNGSTEN(MetalEnum.Tungsten, 3422);

	public static final int OXIDE_MELT_PER_UNIT = 144;
	public static final float OXIDE_COKE_RATIO = 0.5f;
	public static final float OXIDE_FLUX_RATIO = 0.1f;
	public static final float OXIDE_MIN_YIELD = 0.5f;
	public static final float OXIDE_MAX_YIELD = 1.0f;

	private static final int HEAT_FLOOR_POINT = 900;
	private static final int DEGREES_PER_HEAT = 14;

	private final MetalEnum metal;
	private final int meltingPoint;

	BulkBlastFurnaceOxideCharge(MetalEnum metal, int meltingPoint)
	{
		this.metal = metal;
		this.meltingPoint = meltingPoint;
	}

	public MetalEnum getMetal()
	{
		return metal;
	}

	public int getMeltingPoint()
	{
		return meltingPoint;
	}

	public int getTimeFactor()
	{
		return BulkBlastFurnaceCharge.timeFactorFor(meltingPoint);
	}

	public int getHeat()
	{
		return Math.max(0, Math.round((meltingPoint-HEAT_FLOOR_POINT)/(float)DEGREES_PER_HEAT));
	}

	public String getRecipeName()
	{
		return "oxide_pellet_to_molten_"+metal.getName().toLowerCase(Locale.ROOT);
	}
}
