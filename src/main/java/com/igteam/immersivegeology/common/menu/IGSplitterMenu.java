/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import com.igteam.immersivegeology.common.block.belt.entity.IGSplitterBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.SplitterPriority;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class IGSplitterMenu extends AbstractContainerMenu
{
	public static final int FILTER_X = 152;
	public static final int FILTER_Y = 20;
	public static final int INVENTORY_X = 8;
	public static final int INVENTORY_Y = 84;
	public static final int HOTBAR_Y = 142;

	public static final int DATA_INPUT = 0;
	public static final int DATA_OUTPUT = 1;
	public static final int DATA_SIZE = 2;

	public static final int BUTTON_INPUT_BASE = 0;
	public static final int BUTTON_OUTPUT_BASE = 3;

	private static final int FILTER_SLOT = 0;

	private final ContainerData data;
	private final Container filter;
	@Nullable
	private final IGSplitterBlockEntity splitter;
	private final BlockPos pos;

	public IGSplitterMenu(int id, Inventory inventory, IGSplitterBlockEntity splitter)
	{
		this(id, inventory, splitter, splitter.getBlockPos(), new ContainerData()
		{
			@Override
			public int get(int index)
			{
				return switch(index)
				{
					case DATA_INPUT -> splitter.inputPriority().ordinal();
					case DATA_OUTPUT -> splitter.outputPriority().ordinal();
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value)
			{
			}

			@Override
			public int getCount()
			{
				return DATA_SIZE;
			}
		}, new FilterContainer(splitter));
	}

	public IGSplitterMenu(int id, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(id, inventory, null, extraData.readBlockPos(), new SimpleContainerData(DATA_SIZE), new SimpleContainer(1));
	}

	private IGSplitterMenu(int id, Inventory inventory, @Nullable IGSplitterBlockEntity splitter, BlockPos pos, ContainerData data, Container filter)
	{
		super(IGMenuTypes.SPLITTER.get(), id);
		this.splitter = splitter;
		this.pos = pos;
		this.data = data;
		this.filter = filter;
		addDataSlots(data);
		addSlot(new Slot(filter, 0, FILTER_X, FILTER_Y)
		{
			@Override
			public boolean mayPlace(@NotNull ItemStack stack)
			{
				return false;
			}

			@Override
			public boolean mayPickup(@NotNull Player player)
			{
				return false;
			}
		});
		for(int row = 0; row < 3; row++)
			for(int column = 0; column < 9; column++)
				addSlot(new Slot(inventory, column+row*9+9, INVENTORY_X+column*18, INVENTORY_Y+row*18));
		for(int column = 0; column < 9; column++)
			addSlot(new Slot(inventory, column, INVENTORY_X+column*18, HOTBAR_Y));
	}

	public SplitterPriority inputPriority()
	{
		return SplitterPriority.byIndex(data.get(DATA_INPUT));
	}

	public SplitterPriority outputPriority()
	{
		return SplitterPriority.byIndex(data.get(DATA_OUTPUT));
	}

	public boolean hasFilter()
	{
		return !filter.getItem(0).isEmpty();
	}

	@Override
	public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player)
	{
		if(slotId==FILTER_SLOT)
		{
			ItemStack carried = getCarried();
			filter.setItem(0, carried.isEmpty()?ItemStack.EMPTY: carried.copyWithCount(1));
			return;
		}
		super.clicked(slotId, button, clickType, player);
	}

	@Override
	public boolean clickMenuButton(@NotNull Player player, int id)
	{
		if(splitter==null)
			return false;
		if(id >= BUTTON_INPUT_BASE&&id < BUTTON_INPUT_BASE+3)
			splitter.setInputPriority(SplitterPriority.byIndex(id-BUTTON_INPUT_BASE));
		else if(id >= BUTTON_OUTPUT_BASE&&id < BUTTON_OUTPUT_BASE+3)
			splitter.setOutputPriority(SplitterPriority.byIndex(id-BUTTON_OUTPUT_BASE));
		else
			return false;
		return true;
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@NotNull Player player)
	{
		if(splitter!=null&&splitter.isRemoved())
			return false;
		return player.distanceToSqr(pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5) <= 64;
	}

	private static final class FilterContainer extends SimpleContainer
	{
		private final IGSplitterBlockEntity splitter;

		private FilterContainer(IGSplitterBlockEntity splitter)
		{
			super(1);
			this.splitter = splitter;
			super.setItem(0, splitter.filter().copy());
		}

		@Override
		public void setItem(int index, @NotNull ItemStack stack)
		{
			super.setItem(index, stack);
			splitter.setFilter(stack);
		}
	}
}
