/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import java.util.Locale;

public enum BulkBlastFurnaceCharge
{
	HEMATITE(MineralEnum.Hematite, MetalEnum.Iron, 144, 0.5f, 0.25f, 0.2f, 0.9f, 0, 140, true),
	MAGNETITE(MineralEnum.Magnetite, MetalEnum.Iron, 144, 0.5f, 0.25f, 0.2f, 0.95f, 60, 140, true),
	CHALCOPYRITE(MineralEnum.Chalcopyrite, MetalEnum.Copper, 144, 0.5f, 0.5f, 0.15f, 0.85f, 60, 140, false),
	ILMENITE(MineralEnum.Ilmenite, MetalEnum.Titanium, 144, 1.0f, 0.5f, 0.1f, 0.8f, 120, 280, false);

	public static final float PIG_IRON_COKE_RATIO = 1.25f;
	public static final int PIG_IRON_MELT = 160;

	public static final float PELLET_COKE_MULTIPLIER = 0.75f;
	public static final float PELLET_MIN_YIELD_BONUS = 0.15f;
	public static final float PELLET_MAX_YIELD_BONUS = 0.03f;
	public static final float PELLET_TIME_MULTIPLIER = 0.85f;

	public static final float STEEL_COKE_RATIO = 0.1f;
	public static final float STEEL_FLUX_RATIO = 0.5f;
	public static final float STEEL_MIN_YIELD = 0.35f;
	public static final float STEEL_MAX_YIELD = 0.95f;
	public static final int STEEL_UNIT = 144;
	public static final int STEEL_HEAT = 150;
	public static final int STEEL_TIME_FACTOR = 320;

	private final MineralEnum ore;
	private final MetalEnum metal;
	private final int meltPerUnit;
	private final float cokeRatio;
	private final float fluxRatio;
	private final float minYield;
	private final float maxYield;
	private final int heat;
	private final int timeFactor;
	private final boolean carbonRich;

	BulkBlastFurnaceCharge(
			MineralEnum ore, MetalEnum metal, int meltPerUnit, float cokeRatio, float fluxRatio,
			float minYield, float maxYield, int heat, int timeFactor, boolean carbonRich
	)
	{
		this.ore = ore;
		this.metal = metal;
		this.meltPerUnit = meltPerUnit;
		this.cokeRatio = cokeRatio;
		this.fluxRatio = fluxRatio;
		this.minYield = minYield;
		this.maxYield = maxYield;
		this.heat = heat;
		this.timeFactor = timeFactor;
		this.carbonRich = carbonRich;
	}

	public MineralEnum getOre()
	{
		return ore;
	}

	public MetalEnum getMetal()
	{
		return metal;
	}

	public int getMeltPerUnit()
	{
		return meltPerUnit;
	}

	public float getCokeRatio()
	{
		return cokeRatio;
	}

	public float getFluxRatio()
	{
		return fluxRatio;
	}

	public float getMinYield()
	{
		return minYield;
	}

	public float getMaxYield()
	{
		return maxYield;
	}

	public int getHeat()
	{
		return heat;
	}

	public int getTimeFactor()
	{
		return timeFactor;
	}

	public boolean isCarbonRich()
	{
		return carbonRich;
	}

	public boolean hasPelletForm()
	{
		return ore.hasFlag(ItemCategoryFlags.PELLET);
	}

	public float getPelletCokeRatio()
	{
		return cokeRatio*PELLET_COKE_MULTIPLIER;
	}

	public float getPelletMinYield()
	{
		return Math.min(1, minYield+PELLET_MIN_YIELD_BONUS);
	}

	public float getPelletMaxYield()
	{
		return Math.min(1, maxYield+PELLET_MAX_YIELD_BONUS);
	}

	public int getPelletTimeFactor()
	{
		return Math.max(1, Math.round(timeFactor*PELLET_TIME_MULTIPLIER));
	}

	public String getPelletRecipeName()
	{
		return ore.getName().toLowerCase(Locale.ROOT)+"_pellet_to_molten_"+metal.getName().toLowerCase(Locale.ROOT);
	}

	public String getRecipeName()
	{
		return ore.getName().toLowerCase(Locale.ROOT)+"_to_molten_"+metal.getName().toLowerCase(Locale.ROOT);
	}
}
