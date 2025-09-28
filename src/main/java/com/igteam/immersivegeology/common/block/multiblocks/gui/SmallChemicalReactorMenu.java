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
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.ChemicalReactorTanks;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic.SmallChemicalReactorTanks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SmallChemicalReactorMenu extends IEContainerMenu
{
	public final SmallChemicalReactorTanks tanks;
	public final IEnergyStorage energy;
	public GetterAndSetter<Float> damage;

	public static SmallChemicalReactorMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<SmallChemicalReactorLogic.State> ctx)
	{
		final SmallChemicalReactorLogic.State state = ctx.mbContext().getState();
		return new SmallChemicalReactorMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getChemicalReactorTanks(), state.getEnergy(),
				GetterAndSetter.getterOnly(state.getDamage()));
	}

	public static SmallChemicalReactorMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new SmallChemicalReactorMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(3),
				new SmallChemicalReactorTanks(),
				new MutableEnergyStorage(SmallChemicalReactorLogic.ENERGY_CAPACITY), GetterAndSetter.standalone(0f));
	}

	private SmallChemicalReactorMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, SmallChemicalReactorTanks tanks, MutableEnergyStorage energy, GetterAndSetter<Float> damage)
	{
		super(ctx);
		Level level = inventoryPlayer.player.level();
		this.tanks = tanks;
		this.energy = energy;
		this.damage = damage;
		this.addSlot(new IGSlot.SmallChemicalReactorSlot(inv, 0, 191, 5, level));
		this.addSlot(new IESlot.NewOutput(inv, 1, 191, 92));
		this.addSlot(new IGSlot.ChemicalRepairSlot(inv, 2, 80, 89, level));
		ownSlotCount = 3;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 48+j*18, 123+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 48+i*18, 181));

		this.addGenericData(GenericContainerData.energy(energy));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, damage));
		this.addGenericData(GenericContainerData.fluid(tanks.leftInput()));
		this.addGenericData(GenericContainerData.fluid(tanks.rightInput()));
		this.addGenericData(GenericContainerData.fluid(tanks.output()));
	}
}
