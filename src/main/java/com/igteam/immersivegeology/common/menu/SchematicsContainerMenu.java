/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import com.igteam.immersivegeology.common.block.entity.DrawingTableBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class SchematicsMenu extends IEBaseContainerOld<DrawingTableBlockEntity>
{
	public static final int MAX_NUM_DYNAMIC_SLOTS = 20;
	public final Inventory inventoryPlayer;
	private final Level world;
	public SchematicsMenu(MenuType<?> type, int id, Inventory inventoryPlayer, DrawingTableBlockEntity tile)
	{
		super(type, tile, id);
		this.inventoryPlayer = inventoryPlayer;
		this.world = tile.getLevelNonnull();
	}

	private void bindPlayerInv(Inventory inventoryPlayer) {
		int i;
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 145));
		}
	}
}
