/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class ChemicalReactorInputHandler extends InsertOnlyInventory
{
	protected final IItemHandlerModifiable wrapped;
	protected final Runnable onChanged;

	public ChemicalReactorInputHandler(IItemHandlerModifiable wrapped, Runnable onChanged) {
		this.wrapped = wrapped;
		this.onChanged = onChanged;
	}

	public int getSlots() {
		return 1;
	}
}
