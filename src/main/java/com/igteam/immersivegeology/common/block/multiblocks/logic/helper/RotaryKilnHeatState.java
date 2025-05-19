/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public enum RotaryKilnHeatState
{
	HEATING_UP
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					Level level = context.getLevel().getRawLevel();
					int energy = state.total_energy.getEnergyStored();
					float currentHeat = state.getHeat();
					float targetHeat = state.getTargetHeat();
					int processing = state.getProcessorQueue().size();
					int energyCostToRaiseHeat = energyCostForHeat(currentHeat);

					if(energy > energyCostToRaiseHeat)
					{
						state.total_energy.extractEnergy(energyCostToRaiseHeat, false);
						state.modifyHeat(0.7f - (0.05f * processing));
					}
					else {
						state.modifyHeat(-0.5f * processing);
					}
				}
			},
	COOLING_DOWN
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					float currentHeat = state.getHeat();
					if(currentHeat > 0) state.modifyHeat(-0.05f);
					if(currentHeat < 0) state.setHeat(0f);
				}
			},
	MACHINE_OFF
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					float currentHeat = state.getHeat();
					if(currentHeat > 0) state.modifyHeat(-1f);
					if(currentHeat < 0) state.setHeat(0f);
				}
			},
	MAINTAINING_HEAT
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					Level level = context.getLevel().getRawLevel();
					int energy = state.total_energy.getEnergyStored();
					float currentHeat = state.getHeat();
					float targetHeat = state.getTargetHeat();

					int energyCostToRaiseHeat = (int) (energyCostForHeat(currentHeat) * 0.9f);

					if(energy > energyCostToRaiseHeat)
					{
						state.total_energy.extractEnergy(energyCostToRaiseHeat, false);
						float modDir = -1;
						if(currentHeat < targetHeat) modDir = 1;
						state.modifyHeat(modDir * 0.02f);
						return;
					}
					state.modifyHeat(-0.6f);
				}
			},
	RUNNING_RECIPE
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					int energy = state.total_energy.getEnergyStored();
					float currentHeat = state.getHeat();
					int processing = state.getProcessorQueue().size();
					int energyCostToRaiseHeat = (int) (energyCostForHeat(currentHeat) * 0.25f);

					if(energy > energyCostToRaiseHeat)
					{
						state.total_energy.extractEnergy(energyCostToRaiseHeat, false);
						return;
					}
					state.modifyHeat(-0.5f * processing);
				}
			};

	private static final float lv_heat_target = 45;
	private static final float mv_heat_target = 75;
	private static final float hv_heat_target = 145;
	private static final float ehv_heat_target = 165;

	private static final float lv_cost_cutoff = 5f;
	private static final float mv_cost_cutoff = 31;
	private static final float hv_cost_cutoff = 76f;
	private static final float ehv_cost_cutoff = 121;

	public void execute(IMultiblockContext<RotaryKilnLogic.State> context){};

	private static int energyCostForHeat(float heat)
	{
		int upkeepCost = 0;
		if(heat > lv_cost_cutoff) upkeepCost += 250;
		if(heat > mv_cost_cutoff) upkeepCost += 500;
		if(heat > hv_cost_cutoff) upkeepCost += 2250;
		if(heat > ehv_cost_cutoff) upkeepCost += 8000;

		return upkeepCost;
	}

}
