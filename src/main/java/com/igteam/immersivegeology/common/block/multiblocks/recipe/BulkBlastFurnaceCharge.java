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
	HEMATITE(MineralEnum.Hematite, MetalEnum.Iron, 144, 0.5f, 0.25f, 0.2f, 0.9f, 0, 1538, true),
	MAGNETITE(MineralEnum.Magnetite, MetalEnum.Iron, 144, 0.5f, 0.25f, 0.2f, 0.95f, 60, 1538, true),
	PYRITE(MineralEnum.Pyrite, MetalEnum.Iron, 144, 0.6f, 0.6f, 0.1f, 0.75f, 40, 1538, true),
	CHALCOPYRITE(MineralEnum.Chalcopyrite, MetalEnum.Copper, 144, 0.5f, 0.5f, 0.15f, 0.85f, 60, 1085, false),
	CHALCOCITE(MineralEnum.Chalcocite, MetalEnum.Copper, 144, 0.5f, 0.5f, 0.2f, 0.88f, 40, 1085, false),
	CUPRITE(MineralEnum.Cuprite, MetalEnum.Copper, 144, 0.4f, 0.3f, 0.25f, 0.9f, 0, 1085, false),
	CASSITERITE(MineralEnum.Cassiterite, MetalEnum.Tin, 144, 0.45f, 0.25f, 0.3f, 0.92f, 0, 232, false),
	GALENA(MineralEnum.Galena, MetalEnum.Lead, 144, 0.35f, 0.4f, 0.25f, 0.9f, 0, 327, false),
	VANADINITE(MineralEnum.Vanadinite, MetalEnum.Lead, 144, 0.4f, 0.45f, 0.15f, 0.8f, 0, 327, false),
	SMITHSONITE(MineralEnum.Smithsonite, MetalEnum.Zinc, 144, 0.4f, 0.35f, 0.25f, 0.88f, 0, 420, false),
	SPHALERITE(MineralEnum.Sphalerite, MetalEnum.Zinc, 144, 0.5f, 0.5f, 0.2f, 0.85f, 20, 420, false),
	ACANTHITE(MineralEnum.Acanthite, MetalEnum.Silver, 144, 0.4f, 0.4f, 0.2f, 0.85f, 20, 962, false),
	PYROLUSITE(MineralEnum.Pyrolusite, MetalEnum.Manganese, 144, 0.7f, 0.4f, 0.15f, 0.8f, 60, 1246, false),
	MILLERITE(MineralEnum.Millerite, MetalEnum.Nickel, 144, 0.6f, 0.5f, 0.15f, 0.82f, 80, 1455, false),
	ILMENITE(MineralEnum.Ilmenite, MetalEnum.Titanium, 144, 1.0f, 0.5f, 0.1f, 0.8f, 120, 1668, false);

	public static final int TIME_BASE = 40;
	public static final float TIME_PER_DEGREE = 0.065f;

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
	private final int meltingPoint;
	private final boolean carbonRich;

	BulkBlastFurnaceCharge(
			MineralEnum ore, MetalEnum metal, int meltPerUnit, float cokeRatio, float fluxRatio,
			float minYield, float maxYield, int heat, int meltingPoint, boolean carbonRich
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
		this.meltingPoint = meltingPoint;
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

	public int getMeltingPoint()
	{
		return meltingPoint;
	}

	public static int timeFactorFor(int meltingPoint)
	{
		return TIME_BASE+Math.round(meltingPoint*TIME_PER_DEGREE);
	}

	public int getTimeFactor()
	{
		return timeFactorFor(meltingPoint);
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
		return Math.max(1, Math.round(getTimeFactor()*PELLET_TIME_MULTIPLIER));
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
