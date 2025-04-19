/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.common.gui.CrateMenu;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.core.lib.IGLib;
import invtweaks.api.container.ChestContainer;
import mezz.jei.forge.platform.ItemStackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.Iterator;

@ChestContainer
public class IGCrateMenu extends AbstractContainerMenu
{
	private final Container container;
	public final int STACK_MULTIPLIER = 2;
	public static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
	public IGCrateMenu(MenuType<?> type, int id, Inventory inventoryPlayer, Container container) {
		super(type, id);
		ACTIVE.set(true);
		Preconditions.checkArgument(container.getContainerSize() == 27);
		this.container = container;

		int i;
		for(i = 0; i < container.getContainerSize(); ++i) {
			this.addSlot(new CrateSlot(container, i, 8 + i % 9 * 18, 18 + i / 9 * 18, STACK_MULTIPLIER) {
				public boolean mayPlace(ItemStack stack) {
					return IEApi.isAllowedInCrate(stack);
				}
			});
		}

		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 145));
		}
		ACTIVE.set(false);
	}


	public IGCrateMenu(MenuType<?> type, int id, Inventory inventoryPlayer) {
		this(type, id, inventoryPlayer, new CrateContainer(27, 2));
	}

	public boolean stillValid(@Nonnull Player pPlayer) {
		return this.container.stillValid(pPlayer);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack originalStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			originalStack = stackInSlot.copy();

			if (index < 27) {
				if (!moveStackRespectingCustomLimits(stackInSlot, 27, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!moveStackRespectingCustomLimits(stackInSlot, 0, 27, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return originalStack;
	}

	public int getCustomStackLimit(int index, ItemStack stack) {
		if (index < 27)
		{
			if(stack.isDamaged()) return stack.getMaxStackSize();
			return stack.getMaxStackSize() * 2;
		}
		return stack.getMaxStackSize();
	}

	protected boolean moveStackRespectingCustomLimits(ItemStack stackToMove, int startIndex, int endIndex, boolean reverseDirection) {
		boolean changed = false;

		// First try merging with existing stacks
		for (int i = reverseDirection ? endIndex - 1 : startIndex; reverseDirection ? i >= startIndex : i < endIndex; i += reverseDirection ? -1 : 1) {
			Slot slot = this.slots.get(i);
			ItemStack existing = slot.getItem();

			if (!existing.isEmpty() && ItemStack.isSameItemSameTags(stackToMove, existing)) {
				int maxStackSize = getCustomStackLimit(i, existing);
				int space = maxStackSize - existing.getCount();

				if (space > 0) {
					int toTransfer = Math.min(space, stackToMove.getCount());
					existing.grow(toTransfer);
					stackToMove.shrink(toTransfer);
					slot.setChanged();
					changed = true;

					if (stackToMove.isEmpty()) {
						return true;
					}
				}
			}
		}

		// Then try placing into empty slots
		for (int i = reverseDirection ? endIndex - 1 : startIndex; reverseDirection ? i >= startIndex : i < endIndex; i += reverseDirection ? -1 : 1) {
			Slot slot = this.slots.get(i);
			if (!slot.hasItem() && slot.mayPlace(stackToMove)) {
				int maxStackSize = getCustomStackLimit(i, stackToMove);
				ItemStack newStack = stackToMove.split(Math.min(stackToMove.getCount(), maxStackSize));
				slot.set(newStack);
				slot.setChanged();
				changed = true;

				if (stackToMove.isEmpty()) {
					return true;
				}
			}
		}

		return changed;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection)
	{
		return super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
	}

	@Override
	public void setRemoteSlotNoCopy(int pSlot, ItemStack pStack)
	{
		super.setRemoteSlotNoCopy(pSlot, pStack);
	}

	@Override
	public void slotsChanged(Container pContainer)
	{
		super.slotsChanged(pContainer);
	}

	@Override
	public void setItem(int pSlotId, int pStateId, ItemStack pStack)
	{
		super.setItem(pSlotId, pStateId, getSlot(pSlotId).getItem());
	}

	@Override
	public void setRemoteSlot(int pSlot, ItemStack pStack)
	{
		super.setRemoteSlot(pSlot, pStack);
	}

	@Override
	public void setRemoteCarried(ItemStack pRemoteCarried)
	{
		super.setRemoteCarried(pRemoteCarried);
	}

	@Override
	public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer)
	{
		ACTIVE.set(true); // Mark as active
		super.clicked(pSlotId, pButton, pClickType, pPlayer);
		ACTIVE.set(false); // Reset after init
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		for (Slot slot : slots) {
			if (slot.hasItem()) {
				ItemStack stack = slot.getItem();
				if (stack.getCount() > 64) {

					slot.setChanged();
				}
			}
		}
	}

	public static class CrateContainer extends SimpleContainer
	{
		private int slots_num;
		private int stackMult;
		public CrateContainer(int pSize, int stackMult)
		{
			super(pSize);
			this.slots_num = pSize;
			this.stackMult = stackMult;
		}

		@Override
		public int getMaxStackSize() {
			return 64 * stackMult;
		}

		@Override
		public void setItem(int pIndex, ItemStack pStack)
		{
			super.setItem(pIndex, pStack);
		}

		@Override
		public boolean canAddItem(ItemStack pStack)
		{
			boolean canAdd = false;
			for(int index = 0; index < slots_num; index++)
			{
				ItemStack stack = getItem(index);
				int stackMax = stack.getMaxStackSize();
				if (stack.isEmpty() || ItemStack.isSameItemSameTags(stack, pStack) && stack.getCount() < (stack.isDamaged() ? stackMax : stackMax * 2)) {
					canAdd = true;
					break;
				}
			}
			return canAdd;
		}

		@Override
		public ItemStack addItem(ItemStack pStack)
		{
			if (pStack.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				ItemStack $$1 = pStack.copy();
				this.moveItemToOccupiedSlotsWithSameType($$1);
				if ($$1.isEmpty()) {
					return ItemStack.EMPTY;
				} else {
					this.moveItemToEmptySlots($$1);
					return $$1.isEmpty() ? ItemStack.EMPTY : $$1;
				}
			}
		}


		private void moveItemToEmptySlots(ItemStack pStack) {
			for(int $$1 = 0; $$1 < slots_num; ++$$1) {
				ItemStack $$2 = this.getItem($$1);
				if ($$2.isEmpty()) {
					this.setItem($$1, pStack.copyAndClear());
					return;
				}
			}

		}
		private void moveItemToOccupiedSlotsWithSameType(ItemStack pStack) {
			for(int $$1 = 0; $$1 < slots_num; ++$$1) {
				ItemStack $$2 = this.getItem($$1);
				if (ItemStack.isSameItemSameTags($$2, pStack)) {
					this.moveItemsBetweenStacks(pStack, $$2);
					if (pStack.isEmpty()) {
						return;
					}
				}
			}

		}

		private void moveItemsBetweenStacks(ItemStack pStack, ItemStack pOther) {
			int $$2 = Math.min(this.getMaxStackSize(), (pOther.isDamaged() ? pOther.getMaxStackSize() : pOther.getMaxStackSize() * 2));
			int $$3 = Math.min(pStack.getCount(), $$2 - pOther.getCount());
			if ($$3 > 0) {
				pOther.grow($$3);
				pStack.shrink($$3);
				this.setChanged();
			}

		}
	}

	private static class CrateSlot extends Slot
	{
		private final int stackMult;
		public CrateSlot(Container pContainer, int pSlot, int pX, int pY, int stackMult)
		{
			super(pContainer, pSlot, pX, pY);
			this.stackMult = stackMult;
		}

		@Override
		public void set(ItemStack stack) {
			super.set(stack);
		}

		@Override
		protected void onQuickCraft(ItemStack pStack, int pAmount)
		{
			IGLib.IG_LOGGER.info("Craft?");
		}

		@Override
		public ItemStack remove(int amount)
		{
			return super.remove(amount);
		}

		@Override
		public int getMaxStackSize() {
			// Default to 64 if empty or invalid
			ItemStack stack = getItem();
			return stack.isEmpty() ? 64 * stackMult : stack.getMaxStackSize() * stackMult;
		}

		@Override
		public int getMaxStackSize(ItemStack stack) {
			// This is the method Forge uses for merging into an empty slot
			return stack.getMaxStackSize() * stackMult;
		}
	}
}
