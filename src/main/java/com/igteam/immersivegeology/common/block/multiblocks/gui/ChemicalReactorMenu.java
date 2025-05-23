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
import blusunrize.immersiveengineering.common.gui.IESlot.WithPredicate;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.igteam.immersivegeology.client.renderer.multiblocks.ChemicalReactorRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.ChemicalReactorTanks;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.ChemicalRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ChemicalReactorMenu extends IEContainerMenu
{
	public final ChemicalReactorTanks tanks;
	public final IEnergyStorage energy;

	public static ChemicalReactorMenu makeServer(MenuType<?> type, int id, Inventory invPlayer,MultiblockMenuContext<ChemicalReactorLogic.State> ctx)
	{
		final ChemicalReactorLogic.State state = ctx.mbContext().getState();
		return new ChemicalReactorMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getChemicalReactorTanks(), state.getEnergy(),
				GetterAndSetter.standalone(0f));
	}


	public static ChemicalReactorMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new ChemicalReactorMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(2),
				new ChemicalReactorTanks(),
				new MutableEnergyStorage(ChemicalReactorLogic.ENERGY_CAPACITY), GetterAndSetter.standalone(0f));
	}

	private ChemicalReactorMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, ChemicalReactorTanks tanks, MutableEnergyStorage energy, GetterAndSetter<Float> guiProgress)
	{
		super(ctx);
		Level level = inventoryPlayer.player.level();
		this.tanks = tanks;
		this.energy = energy;
		this.addSlot(new IGSlot.ChemicalReactorSlot(inv, 0, 135, 10, level));
		this.addSlot(new IESlot.NewOutput(inv, 1, 177, 93));
		ownSlotCount = 2;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 23+j*18, 128+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 23+i*18, 186));

		this.addGenericData(GenericContainerData.energy(energy));
		this.addGenericData(GenericContainerData.fluid(tanks.backInput()));
		this.addGenericData(GenericContainerData.fluid(tanks.leftInput()));
		this.addGenericData(GenericContainerData.fluid(tanks.rightInput()));
		this.addGenericData(GenericContainerData.fluid(tanks.output()));
	}


}
