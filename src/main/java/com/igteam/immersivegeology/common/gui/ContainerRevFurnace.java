package com.igteam.immersivegeology.common.gui;

import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityRevFurnace;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerRevFurnace extends Container
{
	private final TileEntityRevFurnace tile;
	private final RevFurnaceLogic.State state;

	private final int[] lastValues = new int[8];

	public final int[] clientValues = new int[8];

	public ContainerRevFurnace(InventoryPlayer inventoryPlayer, TileEntityRevFurnace tile)
	{
		this.tile = tile;
		this.state = tile.getState();
		java.util.Arrays.fill(lastValues, -1);

		IItemHandler inv = state.getItemHandler();

		addFurnaceSlots(inv, 0, 36, 67);
		addFurnaceSlots(inv, 3, 116, 147);

		for(int row = 0; row < 3; row++)
			for(int col = 0; col < 9; col++)
				addSlotToContainer(new Slot(inventoryPlayer, col+row*9+9, 8+col*18, 84+row*18));

		for(int col = 0; col < 9; col++)
			addSlotToContainer(new Slot(inventoryPlayer, col, 8+col*18, 142));
	}

	private void addFurnaceSlots(IItemHandler inv, int base, int inputX, int outputX)
	{
		addSlotToContainer(new SlotItemHandler(inv, base, inputX, 17)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return RevFurnaceRecipe.findRecipe(stack)!=null;
			}
		});
		addSlotToContainer(new SlotItemHandler(inv, base+1, inputX, 53)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe
						.isValidBlastFuel(stack);
			}
		});
		addSlotToContainer(new SlotItemHandler(inv, base+2, outputX, 37)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}
		});
	}

	public RevFurnaceLogic.State getState()
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

	private int[] currentValues()
	{
		return new int[]{
				state.furnaceLeft.getProcess(), state.furnaceLeft.getProcessMax(),
				state.furnaceLeft.getBurnTime(), state.furnaceLeft.getLastBurnTime(),
				state.furnaceRight.getProcess(), state.furnaceRight.getProcessMax(),
				state.furnaceRight.getBurnTime(), state.furnaceRight.getLastBurnTime()
		};
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if(state==null) return;

		int[] values = currentValues();
		for(IContainerListener listener : listeners)
			for(int i = 0; i < values.length; i++)
				if(values[i]!=lastValues[i]) listener.sendWindowProperty(this, i, values[i]);

		System.arraycopy(values, 0, lastValues, 0, values.length);
	}

	@Override
	public void updateProgressBar(int id, int data)
	{
		if(id >= 0&&id < clientValues.length) clientValues[id] = data;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		Slot slot = inventorySlots.get(index);
		if(slot==null||!slot.getHasStack()) return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack original = stack.copy();

		if(index < 6)
		{
			if(!mergeItemStack(stack, 6, inventorySlots.size(), true)) return ItemStack.EMPTY;
		}
		else if(RevFurnaceRecipe.findRecipe(stack)!=null)
		{
			if(!mergeItemStack(stack, 0, 1, false)&&!mergeItemStack(stack, 3, 4, false))
				return ItemStack.EMPTY;
		}
		else if(blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe.isValidBlastFuel(stack))
		{
			if(!mergeItemStack(stack, 1, 2, false)&&!mergeItemStack(stack, 4, 5, false))
				return ItemStack.EMPTY;
		}
		else return ItemStack.EMPTY;

		if(stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		else slot.onSlotChanged();

		return original;
	}
}
