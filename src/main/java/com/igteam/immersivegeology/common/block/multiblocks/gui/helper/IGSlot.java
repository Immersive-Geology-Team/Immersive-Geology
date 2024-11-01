/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui.helper;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class IGSlot extends Slot
{
	final AbstractContainerMenu containerMenu;

	IGSlot(AbstractContainerMenu containerMenu, Container inv, int id, int x, int y) {
		super(inv, id, x, y);
		this.containerMenu = containerMenu;
	}

	public static class BloomerySlot extends SlotItemHandler
	{
		private final Level level;

		public BloomerySlot(IItemHandler inv, int id, int x, int y, Level level) {
			super(inv, id, x, y);
			this.level = level;
		}

		public boolean mayPlace(ItemStack itemStack) {
			return BloomeryFuel.isValidBloomeryFuel(this.level, itemStack);
		}
	}
}
