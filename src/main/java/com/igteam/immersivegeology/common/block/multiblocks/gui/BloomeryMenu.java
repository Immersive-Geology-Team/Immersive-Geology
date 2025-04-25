/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGFurnaceHandler.IGFurnaceStateView;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BloomeryMenu extends IEContainerMenu
{
	public final ContainerData state;

	public static BloomeryMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<State> ctx
	)
	{
		final State state = ctx.mbContext().getState();
		return new BloomeryMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(0), state.getStateView());
	}


	public static BloomeryMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new BloomeryMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(BloomeryLogic.NUM_SLOTS),
				new SimpleContainerData(IGFurnaceStateView.NUM_SLOTS)
		);
	}

	private BloomeryMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, ContainerData state)
	{
		super(ctx);
		Level level = inventoryPlayer.player.level();
		this.addSlot(new SlotItemHandler(inv, 0, 51, 17)
		{
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return BloomeryRecipe.findRecipe(level, itemStack, null)!=null;
			}
		});
		this.addSlot(new IGSlot.BloomerySlot(inv, 1, 51, 53, level));
		this.addSlot(new IESlot.NewOutput(inv, 2, 97, 17));
		ownSlotCount = 3;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 84+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 8+i*18, 142));
		this.state = state;
		addDataSlots(state);
	}
}
