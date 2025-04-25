/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CrystallizerMenu extends IEContainerMenu
{
	public final IFluidTank[] tanks;
	public final IEnergyStorage energy;
	public final GetterAndSetter<Float> guiProgress;

	public static CrystallizerMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<CrystallizerLogic.State> ctx)
	{
		final CrystallizerLogic.State state = ctx.mbContext().getState();
		return new CrystallizerMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getInternalTanks(), state.getEnergy(),GetterAndSetter.getterOnly(() -> state.getPercentComplete(ctx.mbContext().getLevel().getRawLevel())));
	}

	public static CrystallizerMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new CrystallizerMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(1),
				new FluidTank[]{new FluidTank(CrystallizerLogic.TANK_VOLUME),new FluidTank(CrystallizerLogic.TANK_VOLUME)},
				new MutableEnergyStorage(CrystallizerLogic.ENERGY_CAPACITY), GetterAndSetter.standalone(0f));
	}

	private CrystallizerMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, FluidTank[] tanks, MutableEnergyStorage energy, GetterAndSetter<Float> guiProgress)
	{
		super(ctx);
		this.tanks = tanks;
		this.energy = energy;
		this.guiProgress = guiProgress;
		this.addSlot(new IESlot.NewOutput(inv, 0, 132, 17));
		ownSlotCount = 1;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 7+j*18, 119+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 7+i*18, 177));

		this.addGenericData(GenericContainerData.energy(energy));
		this.addGenericData(GenericContainerData.fluid(tanks[0]));
		this.addGenericData(GenericContainerData.fluid(tanks[1]));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, guiProgress));
	}
}
