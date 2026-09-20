/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BulkBlastFurnaceLogic;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BulkBlastFurnaceMenu extends IEContainerMenu
{
	public static final int GRID_X = 52;
	public static final int GRID_Y = 18;
	public static final int SLOT_PITCH = 18;
	public static final int PLAYER_INV_Y = 146;
	public static final int PLAYER_INV_X = 7;
	public static final int HOTBAR_Y = 204;

	public final FluidTank metalTank;
	public final FluidTank slagTank;
	public final GetterAndSetter<Float> heat;
	public final GetterAndSetter<Float> quality;
	public final GetterAndSetter<Float> progress;
	public final GetterAndSetter<Float> heatFloor;
	public final GetterAndSetter<Integer> preheaters;
	public final GetterAndSetter<Integer> preheaterStates;
	public final GetterAndSetter<Float> material;
	public final GetterAndSetter<Float> flux;
	public final GetterAndSetter<Float> fluxNeeded;
	public final GetterAndSetter<Float> carbonTarget;
	public final GetterAndSetter<Float> heatCap;
	public final GetterAndSetter<Integer> heatRequired;
	public final GetterAndSetter<Float> fuel;
	public final GetterAndSetter<Float> fuelNeeded;
	public final GetterAndSetter<Integer> status;
	public final GetterAndSetter<Float> heatingProgress;
	public final GetterAndSetter<Boolean> redstoneInput;

	public static BulkBlastFurnaceMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<BulkBlastFurnaceLogic.State> ctx)
	{
		final BulkBlastFurnaceLogic.State state = ctx.mbContext().getState();
		return new BulkBlastFurnaceMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getMetalTank(), state.getSlagTank(),
				GetterAndSetter.getterOnly(state::getHeat),
				GetterAndSetter.getterOnly(state::getQuality),
				GetterAndSetter.getterOnly(state::getProgress),
				GetterAndSetter.getterOnly(state::getHeatFloor),
				GetterAndSetter.getterOnly(state::getPreheaterCount),
				GetterAndSetter.getterOnly(state::getPreheaterStates),
				GetterAndSetter.getterOnly(state::getMaterial),
				GetterAndSetter.getterOnly(state::getFlux),
				GetterAndSetter.getterOnly(state::getFluxNeeded),
				GetterAndSetter.getterOnly(state::getCarbonTargetHeat),
				GetterAndSetter.getterOnly(state::getHeatCap),
				GetterAndSetter.getterOnly(state::getHeatRequired),
				GetterAndSetter.getterOnly(state::getFuel),
				GetterAndSetter.getterOnly(state::getFuelNeeded),
				GetterAndSetter.getterOnly(state::getStatus),
				GetterAndSetter.getterOnly(state::getHeatingProgress),
				GetterAndSetter.getterOnly(state::hasRedstoneInput)
		);
	}

	public static BulkBlastFurnaceMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new BulkBlastFurnaceMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(BulkBlastFurnaceLogic.NUM_INPUT_SLOTS),
				new FluidTank(BulkBlastFurnaceLogic.TOTAL_TANK_CAPACITY),
				new FluidTank(BulkBlastFurnaceLogic.TOTAL_TANK_CAPACITY),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0),
				GetterAndSetter.standalone(0),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(0),
				GetterAndSetter.standalone(0f),
				GetterAndSetter.standalone(false)
		);
	}

	private BulkBlastFurnaceMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, FluidTank metalTank, FluidTank slagTank,
								 GetterAndSetter<Float> heat, GetterAndSetter<Float> quality, GetterAndSetter<Float> progress,
								 GetterAndSetter<Float> heatFloor, GetterAndSetter<Integer> preheaters, GetterAndSetter<Integer> preheaterStates,
								 GetterAndSetter<Float> material, GetterAndSetter<Float> flux,
								 GetterAndSetter<Float> fluxNeeded, GetterAndSetter<Float> carbonTarget, GetterAndSetter<Float> heatCap,
								 GetterAndSetter<Integer> heatRequired, GetterAndSetter<Float> fuel,
								 GetterAndSetter<Float> fuelNeeded, GetterAndSetter<Integer> status,
								 GetterAndSetter<Float> heatingProgress, GetterAndSetter<Boolean> redstoneInput)
	{
		super(ctx);
		this.metalTank = metalTank;
		this.slagTank = slagTank;
		this.heat = heat;
		this.quality = quality;
		this.progress = progress;
		this.heatFloor = heatFloor;
		this.preheaters = preheaters;
		this.preheaterStates = preheaterStates;
		this.material = material;
		this.flux = flux;
		this.fluxNeeded = fluxNeeded;
		this.carbonTarget = carbonTarget;
		this.heatCap = heatCap;
		this.heatRequired = heatRequired;
		this.fuel = fuel;
		this.fuelNeeded = fuelNeeded;
		this.status = status;
		this.heatingProgress = heatingProgress;
		this.redstoneInput = redstoneInput;

		final Level level = inventoryPlayer.player.level();
		for(int row = 0; row < BulkBlastFurnaceLogic.GRID_WIDTH; row++)
			for(int col = 0; col < BulkBlastFurnaceLogic.GRID_WIDTH; col++)
				addSlot(new SlotItemHandler(
						inv, col+row*BulkBlastFurnaceLogic.GRID_WIDTH,
						GRID_X+col*SLOT_PITCH, GRID_Y+row*SLOT_PITCH
				)
				{
					@Override
					public boolean mayPlace(@NotNull ItemStack stack)
					{
						return BulkBlastFurnaceLogic.isChargeComponent(level, stack);
					}
				});

		ownSlotCount = BulkBlastFurnaceLogic.NUM_INPUT_SLOTS;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, PLAYER_INV_X+j*18, PLAYER_INV_Y+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, PLAYER_INV_X+i*18, HOTBAR_Y));

		addGenericData(GenericContainerData.fluid(metalTank));
		addGenericData(GenericContainerData.fluid(slagTank));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heat));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, quality));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, progress));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heatFloor));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, preheaters));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, preheaterStates));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, material));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, flux));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, fluxNeeded));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, carbonTarget));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heatCap));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, heatRequired));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, fuel));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, fuelNeeded));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.INT32, status));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heatingProgress));
		addGenericData(new GenericContainerData<>(GenericDataSerializers.BOOLEAN, redstoneInput));
	}
}
