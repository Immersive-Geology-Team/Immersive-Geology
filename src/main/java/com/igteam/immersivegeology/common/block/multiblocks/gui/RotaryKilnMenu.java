/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.process.RotaryKilnProcess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RotaryKilnMenu extends IEContainerMenu
{
	public final IEnergyStorage energy_lv;
	public final IEnergyStorage energy_mv;
	public final IEnergyStorage energy_hv;
	public final GetterAndSetter<Integer> energyAverage;
	public final GetterAndSetter<Float> heat;
	public final GetterAndSetter<Integer> packed_process_data;

	public static RotaryKilnMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<RotaryKilnLogic.State> ctx)
	{
		final RotaryKilnLogic.State state = ctx.mbContext().getState();

		return new RotaryKilnMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(),
				state.getEnergyLV(), state.getEnergyMV(), state.getEnergyHV(),
				GetterAndSetter.getterOnly(state::getAveragePower),
				GetterAndSetter.getterOnly(state::getHeat),
				GetterAndSetter.getterOnly(() -> getPackedProcessInt(state.getProcessorQueue(), ctx.mbContext().getLevel().getRawLevel()))
		);
	}

	private static int getPackedProcessInt(List<MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>>> processes, Level level)
	{
		int packed = 0;

		for (MultiblockProcess<RotaryKilnRecipe, ProcessContextInMachine<RotaryKilnRecipe>> process : processes) {
			IGSlot.RotarySlot slotObj = IGSlot.RotarySlot.fromCtx((RotaryKilnProcess) process, level);
			int packedPosition = slotObj.slot(); // 1–7
			int processStep = slotObj.processStep(); // 0–15

			if (packedPosition < 1 || packedPosition > 7) {
				throw new IllegalArgumentException("Packed position must be between 1 and 7, got: " + packedPosition);
			}

			int shift = (packedPosition - 1) * 4;
			packed |= (processStep & 0xF) << shift;
		}

		return packed;
	}

	public static RotaryKilnMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new RotaryKilnMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(RotaryKilnLogic.NUM_SLOTS),
				new MutableEnergyStorage(RotaryKilnLogic.ENERGY_CAPACITY),
				new MutableEnergyStorage(RotaryKilnLogic.ENERGY_CAPACITY*2),
				new MutableEnergyStorage(RotaryKilnLogic.ENERGY_CAPACITY*4),
				GetterAndSetter.standalone(0),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0)
		);
	}

	private RotaryKilnMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, MutableEnergyStorage energy_lv, MutableEnergyStorage energy_mv, MutableEnergyStorage energy_hv,  GetterAndSetter<Integer> energyAverage, GetterAndSetter<Float> heat, GetterAndSetter<Integer> packed_process_data)
	{
		super(ctx);
		Level level = inventoryPlayer.player.level();
		this.energyAverage = energyAverage;

		this.packed_process_data = packed_process_data;

		this.energy_lv = energy_lv;
		this.energy_mv = energy_mv;
		this.energy_hv = energy_hv;
		this.heat = heat;

		// Normal Input
		this.addSlot(new SlotItemHandler(inv, 0, 68, 7)
		{
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return RotaryKilnRecipe.findRecipe(level, itemStack)!=null;
			}
		});

		for(int i = 0; i < 7; i++)
		{
			this.addSlot(new SlotItemHandler(inv, 1+i, 14 + i*18, 32)
			{
				@Override
				public boolean mayPlace(@NotNull ItemStack itemStack)
				{
					return false;
				}

				@Override
				public boolean mayPickup(Player playerIn)
				{
					return false;
				}
			});

			this.addSlot(new IESlot.NewOutput(inv, 8 + i, 14+i*18, 76));
		}

		ownSlotCount = 15;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 23+j*18, 109+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 23+i*18, 167));

		this.addGenericData(GenericContainerData.energy(energy_lv));
		this.addGenericData(GenericContainerData.energy(energy_mv));
		this.addGenericData(GenericContainerData.energy(energy_hv));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, energyAverage));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heat));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, packed_process_data));

	}
}
