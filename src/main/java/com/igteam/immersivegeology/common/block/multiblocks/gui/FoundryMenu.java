/*
 * Muddykat
 * Copyright (c) 2025
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
import com.igteam.immersivegeology.common.block.multiblocks.logic.FoundryLogic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public class FoundryMenu extends IEContainerMenu
{
	public static final int MOLD_X = 22;
	public static final int MOLD_Y = 74;
	public static final int OUTPUT_X = 65;
	public static final int OUTPUT_Y = 73;
	public static final int OUTPUT_PITCH = 19;
	public static final int PLAYER_INV_X = 7;
	public static final int PLAYER_INV_Y = 146;
	public static final int HOTBAR_Y = 204;
	private static final String PURGE_KEY = "purge_tank";

	public final FluidTank[] tanks;
	public final MutableEnergyStorage energy;
	public final GetterAndSetter<Float> progress;
	public final GetterAndSetter<Float> alloyProgress;
	public final GetterAndSetter<Boolean> alloying;
	public final GetterAndSetter<Boolean> redstoneInput;
	private final IntConsumer purge;

	public static FoundryMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<FoundryLogic.State> ctx)
	{
		final FoundryLogic.State state = ctx.mbContext().getState();
		return new FoundryMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getTanks(), state.getEnergy(),
				GetterAndSetter.getterOnly(state::getProgress),
				GetterAndSetter.getterOnly(state::getAlloyProgress),
				GetterAndSetter.getterOnly(state::isAlloying),
				GetterAndSetter.getterOnly(state::hasRedstoneInput), state::purgeTank
		);
	}

	private static FluidTank[] clientTanks()
	{
		FluidTank[] tanks = new FluidTank[FoundryLogic.TANK_COUNT];
		for(int i = 0; i < tanks.length; i++) tanks[i] = new FluidTank(FoundryLogic.TANK_VOLUME);
		return tanks;
	}

	public static FoundryMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new FoundryMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(FoundryLogic.SLOT_COUNT),
				clientTanks(),
				new MutableEnergyStorage(FoundryLogic.ENERGY_CAPACITY),
				GetterAndSetter.standalone(0f), GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(false), GetterAndSetter.standalone(false), index -> {}
		);
	}

	private FoundryMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, FluidTank[] tanks, MutableEnergyStorage energy, GetterAndSetter<Float> progress, GetterAndSetter<Float> alloyProgress, GetterAndSetter<Boolean> alloying, GetterAndSetter<Boolean> redstoneInput, IntConsumer purge)
	{
		super(ctx);
		this.tanks = tanks;
		this.energy = energy;
		this.progress = progress;
		this.alloyProgress = alloyProgress;
		this.alloying = alloying;
		this.redstoneInput = redstoneInput;
		this.purge = purge;

		addSlot(new SlotItemHandler(inv, FoundryLogic.MOLD_SLOT, MOLD_X, MOLD_Y)
		{
			@Override
			public boolean mayPlace(@NotNull ItemStack stack)
			{
				return FoundryLogic.isMold(stack);
			}
		});
		for(int i = 0; i < FoundryLogic.OUTPUT_SLOTS; i++)
			addSlot(new IESlot.NewOutput(inv, FoundryLogic.FIRST_OUTPUT_SLOT+i, OUTPUT_X+i*OUTPUT_PITCH, OUTPUT_Y));

		ownSlotCount = FoundryLogic.SLOT_COUNT;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, PLAYER_INV_X+j*18, PLAYER_INV_Y+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, PLAYER_INV_X+i*18, HOTBAR_Y));

		addGenericData(GenericContainerData.energy(energy));
		for(FluidTank tank : tanks) addGenericData(GenericContainerData.fluid(tank));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, progress));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, alloyProgress));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.BOOLEAN, alloying));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.BOOLEAN, redstoneInput));
	}

	public static CompoundTag purgeMessage(int tank)
	{
		CompoundTag message = new CompoundTag();
		message.putInt(PURGE_KEY, tank);
		return message;
	}

	@Override
	public void receiveMessageFromScreen(CompoundTag message)
	{
		if(message.contains(PURGE_KEY)) purge.accept(message.getInt(PURGE_KEY));
	}
}
