/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInMachine;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.common.block.multiblocks.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalExchangerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RotaryKilnRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.process.RotaryKilnProcess;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeothermalExchangerMenu extends IEContainerMenu
{
	public final GetterAndSetter<Float> display_heat;
	public final GetterAndSetter<Float> cooling_rate;
	public final IEnergyStorage energy_storage;
	public final GetterAndSetter<byte[]> BLOCK_MAP_DATA;
	public final IFluidTank[] tanks;

	public static GeothermalExchangerMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<GeothermalExchangerLogic.State> ctx)
	{
		final GeothermalExchangerLogic.State state = ctx.mbContext().getState();
		final IMultiblockLevel multiblockLevel = ctx.mbContext().getLevel();
		return new GeothermalExchangerMenu(multiblockCtx(type, id, ctx), invPlayer,
				state.getInternalTanks(),
				state.getEnergy(),
				GetterAndSetter.getterOnly(state::getDisplayHeat), GetterAndSetter.getterOnly(state::getCoolingRate), GetterAndSetter.getterOnly(state::getHeatingStates));
	}

	private static int[] calculateStructureDimensions(List<StructureTemplate.StructureBlockInfo> structure)
	{
		int structureHeight = 0;
		int structureWidth = 0;
		int structureLength = 0;

		for(StructureTemplate.StructureBlockInfo block : structure)
		{
			structureHeight = Math.max(structureHeight, block.pos().getY()+1);
			structureWidth = Math.max(structureWidth, block.pos().getZ()+1);
			structureLength = Math.max(structureLength, block.pos().getX()+1);
		}

		return new int[]{structureHeight, structureWidth, structureLength};
	}

	public static GeothermalExchangerMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new GeothermalExchangerMenu(clientCtx(type, id), invPlayer,new FluidTank[]{new FluidTank(GeothermalExchangerLogic.TANK_VOLUME),new FluidTank(GeothermalExchangerLogic.TANK_VOLUME)}, new MutableEnergyStorage(GeothermalExchangerLogic.ENERGY_CAPACITY), GetterAndSetter.standalone(0f), GetterAndSetter.standalone(0f), GetterAndSetter.standalone(new byte[66]));
	}

	private GeothermalExchangerMenu(MenuContext ctx, Inventory inventoryPlayer, FluidTank[] tanks, MutableEnergyStorage energy_storage, GetterAndSetter<Float> heat, GetterAndSetter<Float> cooling_rate, GetterAndSetter<byte[]> blockMapData)
	{
		super(ctx);
		this.tanks = tanks;
		this.BLOCK_MAP_DATA = blockMapData;
		this.energy_storage = energy_storage;
		this.display_heat = heat;
		this.cooling_rate = cooling_rate;
		ownSlotCount = 0;
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 37+j*18, 104+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 37+i*18, 162));

		this.addGenericData(GenericContainerData.energy(energy_storage));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, heat));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, cooling_rate));
		this.addGenericData(GenericContainerData.fluid(tanks[0]));
		this.addGenericData(GenericContainerData.fluid(tanks[1]));
		this.addGenericData(new GenericContainerData<>(GenericDataSerializers.BYTE_ARRAY, blockMapData));
	}

	public IEnergyStorage getEnergyStorage()
	{
		return energy_storage;
	}
}