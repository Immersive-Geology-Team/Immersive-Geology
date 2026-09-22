package com.igteam.immersivegeology.common.gui;

import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBloomery;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBloomery extends Container
{
	private final TileEntityBloomery tile;
	private final BloomeryLogic.State state;

	private int lastProcess = -1;
	private int lastProcessMax = -1;
	private int lastBurn = -1;
	private int lastBurnMax = -1;

	public ContainerBloomery(InventoryPlayer inventoryPlayer, TileEntityBloomery tile)
	{
		this.tile = tile;
		this.state = tile.getState();

		IItemHandler inv = state.getItemHandler();

		addSlotToContainer(new SlotItemHandler(inv, 0, 51, 17)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return BloomeryRecipe.findRecipe(stack)!=null;
			}
		});
		addSlotToContainer(new SlotItemHandler(inv, 1, 51, 53)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return BloomeryFuel.isValidBloomeryFuel(stack);
			}
		});
		addSlotToContainer(new SlotItemHandler(inv, 2, 97, 17)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}
		});

		for(int row = 0; row < 3; row++)
			for(int col = 0; col < 9; col++)
				addSlotToContainer(new Slot(inventoryPlayer, col+row*9+9, 8+col*18, 84+row*18));

		for(int col = 0; col < 9; col++)
			addSlotToContainer(new Slot(inventoryPlayer, col, 8+col*18, 142));
	}

	public BloomeryLogic.State getState()
	{
		return state;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return !tile.isInvalid()&&tile.formed
				&&player.getDistanceSq(tile.getPos().getX()+0.5, tile.getPos().getY()+0.5,
				tile.getPos().getZ()+0.5) <= 64;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if(state==null) return;

		int process = state.furnace.getProcess();
		int processMax = state.furnace.getProcessMax();
		int burn = state.furnace.getBurnTime();
		int burnMax = state.furnace.getLastBurnTime();

		for(IContainerListener listener : listeners)
		{
			if(process!=lastProcess) listener.sendWindowProperty(this, 0, process);
			if(processMax!=lastProcessMax) listener.sendWindowProperty(this, 1, processMax);
			if(burn!=lastBurn) listener.sendWindowProperty(this, 2, burn);
			if(burnMax!=lastBurnMax) listener.sendWindowProperty(this, 3, burnMax);
		}

		lastProcess = process;
		lastProcessMax = processMax;
		lastBurn = burn;
		lastBurnMax = burnMax;
	}

	@Override
	public void updateProgressBar(int id, int data)
	{
		switch(id)
		{
			case 0 -> clientProcess = data;
			case 1 -> clientProcessMax = data;
			case 2 -> clientBurn = data;
			case 3 -> clientBurnMax = data;
		}
	}

	public int clientProcess;
	public int clientProcessMax;
	public int clientBurn;
	public int clientBurnMax;

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		Slot slot = inventorySlots.get(index);
		if(slot==null||!slot.getHasStack()) return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack original = stack.copy();

		if(index < 3)
		{
			if(!mergeItemStack(stack, 3, inventorySlots.size(), true)) return ItemStack.EMPTY;
		}
		else if(BloomeryRecipe.findRecipe(stack)!=null)
		{
			if(!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
		}
		else if(BloomeryFuel.isValidBloomeryFuel(stack))
		{
			if(!mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
		}
		else return ItemStack.EMPTY;

		if(stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		else slot.onSlotChanged();

		return original;
	}
}
