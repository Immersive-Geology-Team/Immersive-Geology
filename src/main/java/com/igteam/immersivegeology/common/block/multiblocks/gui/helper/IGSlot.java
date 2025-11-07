/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui.helper;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceProcess;
import blusunrize.immersiveengineering.common.gui.ArcFurnaceMenu;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.process.RotaryKilnProcess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

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

	public static record RotarySlot(int slot, int processStep) {
		public RotarySlot(int slot, int processStep) {
			this.slot = slot;
			this.processStep = processStep;
		}

		public static RotarySlot fromCtx(RotaryKilnProcess process, Level level) {
			float mod = (float)process.processTick / (float)process.getMaxTicks(level);
			int h = (int)Math.max(1.0F, mod * 16.0F);
			return new RotarySlot(process.getSlot(), h);
		}

		public int processStep() {
			return this.processStep;
		}
	}

	public static class ReverberationSlot extends SlotItemHandler
	{
		private final Level level;

		public ReverberationSlot(IItemHandler inv, int id, int x, int y, Level level) {
			super(inv, id, x, y);
			this.level = level;
		}

		public boolean mayPlace(ItemStack itemStack) {
			return BlastFurnaceFuel.isValidBlastFuel(this.level, itemStack);
		}
	}

	public static class ChemicalReactorSlot extends SlotItemHandler
	{
		private final Level level;

		public ChemicalReactorSlot(IItemHandler inv, int id, int x, int y, Level level) {
			super(inv, id, x, y);
			this.level = level;
		}

		public boolean mayPlace(ItemStack itemStack) {
			return ChemicalRecipe.acceptableCatalyst(this.level, itemStack);
		}
	}

	public static class ChemicalRepairSlot extends SlotItemHandler
	{
		private final Level level;

		public ChemicalRepairSlot(IItemHandler inv, int id, int x, int y, Level level) {
			super(inv, id, x, y);
			this.level = level;
		}

		public boolean mayPlace(ItemStack itemStack) {
			return ChemicalRepairRecipe.isValidRepairItem(this.level, itemStack);
		}
	}

	public static class SmallChemicalReactorSlot extends SlotItemHandler
	{
		private final Level level;

		public SmallChemicalReactorSlot(IItemHandler inv, int id, int x, int y, Level level) {
			super(inv, id, x, y);
			this.level = level;
		}

		public boolean mayPlace(@NotNull ItemStack itemStack) {
			return BasicChemicalRecipe.acceptableCatalyst(this.level, itemStack);
		}
	}
}
