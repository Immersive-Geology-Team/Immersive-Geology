/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.IEApi;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.core.lib.IGLib;
import invtweaks.api.container.ChestContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntity.CONTAINER_SIZE;

@ChestContainer
public class IGCrateMenu extends AbstractContainerMenu
{
	private final Container container;
	public IGCrateMenu(MenuType<?> type, int id, Inventory inventoryPlayer, Container container) {
		super(type, id);
		Preconditions.checkArgument(container.getContainerSize() == CONTAINER_SIZE);
		this.container = container;
		int i;
		for(i = 0; i < container.getContainerSize(); ++i) {
			this.addSlot(new Slot(container, i, 8 + i % 9 * 18, 18 + i / 9 * 18) {
				public boolean mayPlace(ItemStack stack) {
					return IEApi.isAllowedInCrate(stack);
				}
			});
		}

		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 105 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 163));
		}
	}


	public IGCrateMenu(MenuType<?> type, int id, Inventory inventoryPlayer) {
		this(type, id, inventoryPlayer, new SimpleContainer(CONTAINER_SIZE));
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i)
	{
		ItemStack stackInSlot = ItemStack.EMPTY;
		Slot slot = this.slots.get(i);
		if (slot.hasItem()) {
			ItemStack itemInSlot = slot.getItem();
			stackInSlot = itemInSlot.copy();
			if (i < CONTAINER_SIZE) {
				if (!this.moveItemStackTo(itemInSlot, CONTAINER_SIZE, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemInSlot, 0, CONTAINER_SIZE, false)) {
				return ItemStack.EMPTY;
			}

			slot.setChanged();
		}

		return stackInSlot;
	}

	public boolean stillValid(@Nonnull Player pPlayer) {
		return this.container.stillValid(pPlayer);
	}
}
