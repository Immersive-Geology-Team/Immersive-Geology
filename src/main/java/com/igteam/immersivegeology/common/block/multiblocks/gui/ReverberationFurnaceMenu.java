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
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.RevStateView;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.mojang.datafixers.util.Pair;
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

public class ReverberationFurnaceMenu extends IEContainerMenu
{
	public final ContainerData state;

	public static ReverberationFurnaceMenu makeServer(MenuType<?> type, int id, Inventory invPlayer, MultiblockMenuContext<RevFurnaceLogic.State> ctx
	)
	{
		final RevFurnaceLogic.State state = ctx.mbContext().getState();
		return new ReverberationFurnaceMenu(
				multiblockCtx(type, id, ctx), invPlayer,
				state.getInventory(), state.getStateView());
	}


	public static ReverberationFurnaceMenu makeClient(MenuType<?> type, int id, Inventory invPlayer)
	{
		return new ReverberationFurnaceMenu(
				clientCtx(type, id), invPlayer,
				new ItemStackHandler(RevFurnaceLogic.NUM_SLOTS),
				new SimpleContainerData(RevStateView.NUM_SLOTS)
		);
	}

	private ReverberationFurnaceMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inv, ContainerData state)
	{
		super(ctx);
		Level level = inventoryPlayer.player.level();
		boolean isMirror = false;
		// * Left
		int leftInputX = 36;
		int leftFuelX = 36;
		int leftOutputX = 67;

		this.addSlot(new SlotItemHandler(inv, 0, leftInputX, 17)
		{
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return RevFurnaceRecipe.findRecipe(level, itemStack, null)!=null;
			}
		});
		this.addSlot(new IGSlot.ReverberationSlot(inv, 1, leftFuelX, 53, level));
		this.addSlot(new IESlot.NewOutput(inv, 2, leftOutputX, 37));

		// * Right
		int rightInputX = 116;
		int rightFuelX = 116;
		int rightOutputX = 147;

		this.addSlot(new SlotItemHandler(inv, 3, rightInputX, 17)
		{
			@Override
			public boolean mayPlace(ItemStack itemStack)
			{
				return RevFurnaceRecipe.findRecipe(level, itemStack, null)!=null;
			}
		});
		this.addSlot(new IGSlot.ReverberationSlot(inv, 4, rightFuelX, 53, level));
		this.addSlot(new IESlot.NewOutput(inv, 5, rightOutputX, 37));

		ownSlotCount = 6;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 84+i*18));
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventoryPlayer, i, 8+i*18, 142));

		this.state = state;
		addDataSlots(state);
	}
}
