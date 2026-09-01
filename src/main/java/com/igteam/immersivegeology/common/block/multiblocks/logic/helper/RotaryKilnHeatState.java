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

/**
 * Drives the kiln's heat each tick. Power buys heat, recipes spend it; every state either
 * pays its upkeep out of the energy buffer or bleeds heat at {@link #PASSIVE_COOL_RATE}.
 */
public enum RotaryKilnHeatState
{
	HEATING_UP
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					if(!payUpkeep(state, HEATING_UPKEEP))
					{
						state.modifyHeat(-PASSIVE_COOL_RATE);
						return;
					}
					// A fuller kiln has more mass to bring up to temperature, so it heats slower
					int processing = state.getProcessorQueue().size();
					state.modifyHeat(BASE_HEAT_RATE-(HEAT_RATE_LOAD_PENALTY*processing));
				}
			},
	COOLING_DOWN
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					context.getState().modifyHeat(-PASSIVE_COOL_RATE);
				}
			},
	MACHINE_OFF
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					context.getState().modifyHeat(-PASSIVE_COOL_RATE);
				}
			},
	MAINTAINING_HEAT
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					if(!payUpkeep(state, IDLE_UPKEEP))
					{
						state.modifyHeat(-PASSIVE_COOL_RATE);
						return;
					}
					state.modifyHeat(state.getHeat() < state.getTargetHeat()? HOLD_HEAT_RATE: -HOLD_HEAT_RATE);
				}
			},
	RUNNING_RECIPE
			{
				@Override
				public void execute(IMultiblockContext<State> context)
				{
					State state = context.getState();
					// Heat itself is drawn down by the processes, see State#consumeProcessHeat
					if(!payUpkeep(state, runningUpkeep(state.getProcessorQueue().size())))
						state.modifyHeat(-PASSIVE_COOL_RATE);
				}
			};

	/**
	 * The one rate at which heat bleeds away whenever the kiln is not paying upkeep, whether
	 * that is because it is too hot, switched off, or out of power. Cooling is the same
	 * process in all three cases, so it runs at the same speed in all three.
	 */
	private static final float PASSIVE_COOL_RATE = 0.05f;
	/** Heat gained per tick while climbing to the target, before the load penalty below. */
	private static final float BASE_HEAT_RATE = 0.7f;
	private static final float HEAT_RATE_LOAD_PENALTY = 0.05f;
	/** How hard the kiln nudges itself back onto the target once it is sitting on it. */
	private static final float HOLD_HEAT_RATE = 0.02f;

	/** Reaching a heat tier costs the full input power that tier is defined by. */
	private static final float HEATING_UPKEEP = 1.00f;
	/** Holding a tier with nothing to process is cheaper. */
	private static final float IDLE_UPKEEP = 0.33f;
	/** Running costs between these, scaling with how much of the queue is in use. */
	private static final float MIN_RUNNING_UPKEEP = 0.25f;
	private static final float MAX_RUNNING_UPKEEP = 0.75f;

	private static final float lv_cost_cutoff = 5f;
	private static final float mv_cost_cutoff = 31;
	private static final float hv_cost_cutoff = 76f;
	private static final float ehv_cost_cutoff = 121;

	public abstract void execute(IMultiblockContext<RotaryKilnLogic.State> context);

	/**
	 * Upkeep for a kiln running {@code processing} recipes, ramping from
	 * {@link #MIN_RUNNING_UPKEEP} at one recipe to {@link #MAX_RUNNING_UPKEEP} at a full queue.
	 */
	private static float runningUpkeep(int processing)
	{
		int slots = RotaryKilnLogic.MAX_PROCESSES;
		if(slots < 2) return MAX_RUNNING_UPKEEP;
		int clamped = Math.max(1, Math.min(processing, slots));
		return MIN_RUNNING_UPKEEP+(MAX_RUNNING_UPKEEP-MIN_RUNNING_UPKEEP)*((float)(clamped-1)/(slots-1));
	}

	/**
	 * Draws this tick's upkeep out of the buffer, returning false (drawing nothing) if the
	 * kiln cannot cover it in full.
	 */
	private static boolean payUpkeep(State state, float upkeepFraction)
	{
		int cost = Math.round(energyCostForHeat(state.getHeat())*upkeepFraction);
		if(cost <= 0) return true;
		if(state.getEnergy().getEnergyStored() < cost) return false;
		state.getEnergy().extractEnergy(cost, false);
		return true;
	}

	/**
	 * Power needed to hold the kiln at {@code heat}. Each step is set so that the cost at a
	 * tier's cap equals the input power that tier is reached at, see RotaryKilnLogic.
	 */
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
