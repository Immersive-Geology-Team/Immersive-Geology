/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetectorEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IGMetalDetectorItem extends BlockItem
{
	public static final String ENERGY_TAG = "energy";
	private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

	public IGMetalDetectorItem(Block block)
	{
		super(block, new Properties().stacksTo(1));
	}

	public static int getEnergyStored(ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		if(tag==null||!tag.contains(BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) return 0;
		return Math.min(IGMetalDetectorEntity.ENERGY_CAPACITY, tag.getCompound(BLOCK_ENTITY_TAG).getInt(ENERGY_TAG));
	}

	private static void setEnergyStored(ItemStack stack, int energy)
	{
		CompoundTag tag = stack.getOrCreateTag();
		if(!tag.contains(BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) tag.put(BLOCK_ENTITY_TAG, new CompoundTag());
		tag.getCompound(BLOCK_ENTITY_TAG).putInt(ENERGY_TAG, energy);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip,
								@NotNull TooltipFlag flag)
	{
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("gui.immersivegeology.metal_detector.energy",
				getEnergyStored(stack), IGMetalDetectorEntity.ENERGY_CAPACITY));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack)
	{
		return getEnergyStored(stack) > 0;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack)
	{
		return Math.round(13f*getEnergyStored(stack)/IGMetalDetectorEntity.ENERGY_CAPACITY);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return 0xD54B18;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new EnergyProvider(stack);
	}

	private static class EnergyProvider implements ICapabilityProvider, IEnergyStorage
	{
		private final ItemStack stack;
		private final LazyOptional<IEnergyStorage> capability = LazyOptional.of(() -> this);

		private EnergyProvider(ItemStack stack)
		{
			this.stack = stack;
		}

		@NotNull
		@Override
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
		{
			return ForgeCapabilities.ENERGY.orEmpty(capability, this.capability);
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			int stored = getEnergyStored();
			int accepted = Math.min(getMaxEnergyStored()-stored,
					Math.min(IGMetalDetectorEntity.ENERGY_MAX_INPUT, maxReceive));
			if(accepted <= 0) return 0;
			if(!simulate) setEnergyStored(stack, stored+accepted);
			return accepted;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			return 0;
		}

		@Override
		public int getEnergyStored()
		{
			return IGMetalDetectorItem.getEnergyStored(stack);
		}

		@Override
		public int getMaxEnergyStored()
		{
			return IGMetalDetectorEntity.ENERGY_CAPACITY;
		}

		@Override
		public boolean canExtract()
		{
			return false;
		}

		@Override
		public boolean canReceive()
		{
			return true;
		}
	}
}
