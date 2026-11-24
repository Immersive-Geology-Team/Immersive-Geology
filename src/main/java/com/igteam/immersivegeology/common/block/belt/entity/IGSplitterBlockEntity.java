/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.entity;

import com.igteam.immersivegeology.common.block.belt.helper.SplitterPriority;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.menu.IGSplitterMenu;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class IGSplitterBlockEntity extends IGBeltBlockEntity implements MenuProvider
{
	private static final String INPUT_PRIORITY = "input_priority";
	private static final String OUTPUT_PRIORITY = "output_priority";
	private static final String FILTER = "filter";

	private SplitterPriority inputPriority = SplitterPriority.NONE;
	private SplitterPriority outputPriority = SplitterPriority.NONE;
	private ItemStack filter = ItemStack.EMPTY;

	public IGSplitterBlockEntity(BlockPos pos, BlockState state)
	{
		super(IGRegistrationHolder.SPLITTER.get(), pos, state);
	}

	public SplitterPriority inputPriority()
	{
		return inputPriority;
	}

	public SplitterPriority outputPriority()
	{
		return outputPriority;
	}

	public ItemStack filter()
	{
		return filter;
	}

	public boolean hasFilter()
	{
		return !filter.isEmpty();
	}

	private final java.util.EnumSet<Lane> alternate = java.util.EnumSet.noneOf(Lane.class);

	public boolean alternateFor(Lane lane)
	{
		return alternate.contains(lane);
	}

	public void flipAlternate(Lane lane)
	{
		if(!alternate.remove(lane))
			alternate.add(lane);
	}

		public void setInputPriority(SplitterPriority priority)
	{
		inputPriority = priority;
		setChanged();
	}

	public void setOutputPriority(SplitterPriority priority)
	{
		outputPriority = priority;
		setChanged();
	}

	public void setFilter(ItemStack stack)
	{
		filter = stack.isEmpty()?ItemStack.EMPTY: stack.copyWithCount(1);
		setChanged();
	}

	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		inputPriority = tag.contains(INPUT_PRIORITY)?SplitterPriority.byIndex(tag.getInt(INPUT_PRIORITY)): SplitterPriority.NONE;
		outputPriority = tag.contains(OUTPUT_PRIORITY)?SplitterPriority.byIndex(tag.getInt(OUTPUT_PRIORITY)): SplitterPriority.NONE;
		filter = tag.contains(FILTER)?ItemStack.of(tag.getCompound(FILTER)): ItemStack.EMPTY;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.putInt(INPUT_PRIORITY, inputPriority.ordinal());
		tag.putInt(OUTPUT_PRIORITY, outputPriority.ordinal());
		if(!filter.isEmpty())
			tag.put(FILTER, filter.save(new CompoundTag()));
	}

	@Override
	public @NotNull Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}

	@Override
	public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player)
	{
		return new IGSplitterMenu(id, inventory, this);
	}
}
