package com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class IGFurnaceHandler<R>
{
	public interface IFurnaceEnvironment<R>
	{
		IItemHandlerModifiable getInventory(int furnaceIndex);

		@Nullable
		R getRecipeForInput(int furnaceIndex);

		int getBurnTimeOf(World world, ItemStack fuel);

		default int getProcessSpeed(IMultiblockLevel level, int furnaceIndex)
		{
			return 1;
		}

		default void turnOff(IMultiblockLevel level, int furnaceIndex)
		{
		}
	}

	public static class InputSlot<R>
	{
		private final Function<R, IngredientStack> getFromRecipe;
		final int slotIndex;

		public InputSlot(Function<R, IngredientStack> getFromRecipe, int slotIndex)
		{
			this.getFromRecipe = getFromRecipe;
			this.slotIndex = slotIndex;
		}

		public IngredientStack get(R recipe)
		{
			return getFromRecipe.apply(recipe);
		}
	}

	public static class OutputSlot<R>
	{
		private final Function<R, ItemStack> getFromRecipe;
		final int slotIndex;

		public OutputSlot(Function<R, ItemStack> getFromRecipe, int slotIndex)
		{
			this.getFromRecipe = getFromRecipe;
			this.slotIndex = slotIndex;
		}

		public ItemStack get(R recipe)
		{
			return getFromRecipe.apply(recipe);
		}
	}

	private int process = 0;
	private int processMax = 0;
	private int burnTime = 0;
	private int lastBurnTime = 0;

	private final int fuelSlot;
	private final List<InputSlot<R>> inputs;
	private final List<OutputSlot<R>> outputs;
	private final ToIntFunction<R> getProcessingTime;
	private final Runnable setChanged;

	public IGFurnaceHandler(int fuelSlot, List<InputSlot<R>> inputs, List<OutputSlot<R>> outputs,
							ToIntFunction<R> getProcessingTime, Runnable setChanged)
	{
		this.fuelSlot = fuelSlot;
		this.inputs = inputs;
		this.outputs = outputs;
		this.getProcessingTime = getProcessingTime;
		this.setChanged = setChanged;
	}

	public int getProcess()
	{
		return process;
	}

	public int getProcessMax()
	{
		return processMax;
	}

	public int getBurnTime()
	{
		return burnTime;
	}

	public int getLastBurnTime()
	{
		return lastBurnTime;
	}

	public boolean tickServer(IMultiblockContext<? extends IFurnaceEnvironment<R>> ctx, int furnaceIndex)
	{
		boolean active = false;
		final IFurnaceEnvironment<R> env = ctx.getState();

		if(burnTime > 0)
		{
			int processSpeed = 1;
			if(process > 0) processSpeed = env.getProcessSpeed(ctx.getLevel(), furnaceIndex);
			burnTime -= processSpeed;

			if(process > 0)
			{
				if(isAnyInputEmpty(env.getInventory(furnaceIndex)))
				{
					process = 0;
					processMax = 0;
				}
				else
				{
					R recipe = getRecipe(env, furnaceIndex);
					if(recipe!=null&&getProcessingTime.applyAsInt(recipe)!=processMax)
					{
						processMax = 0;
						process = 0;
					}
					else
					{
						process -= processSpeed;
						processSpeed = 0;
						active = true;
					}
				}
				setChanged.run();
			}

			if(process <= 0)
			{
				if(processMax > 0)
				{
					doRecipeIO(env, furnaceIndex);
					processMax = 0;
					burnTime -= process;
				}
				R recipe = getRecipe(env, furnaceIndex);
				if(recipe!=null)
				{
					final int time = getProcessingTime.applyAsInt(recipe);
					this.process = time-processSpeed;
					this.processMax = time;
					active = true;
				}
			}
		}

		if(burnTime <= 0&&getRecipe(env, furnaceIndex)!=null)
		{
			final IItemHandlerModifiable inv = env.getInventory(furnaceIndex);
			final ItemStack fuel = inv.getStackInSlot(fuelSlot);
			final int addedBurntime = env.getBurnTimeOf(ctx.getLevel().getRawLevel(), fuel);
			if(addedBurntime > 0)
			{
				lastBurnTime = addedBurntime;
				burnTime += lastBurnTime;
				if(fuel.getItem().hasContainerItem(fuel)&&fuel.getCount()==1)
					inv.setStackInSlot(fuelSlot, fuel.getItem().getContainerItem(fuel));
				else
					fuel.shrink(1);
				setChanged.run();
			}
		}

		if(!active) env.turnOff(ctx.getLevel(), furnaceIndex);
		return active;
	}

	public NBTTagCompound toNBT(int index)
	{
		final NBTTagCompound result = new NBTTagCompound();
		result.setInteger("process"+index, process);
		result.setInteger("processMax"+index, processMax);
		result.setInteger("burnTime"+index, burnTime);
		result.setInteger("lastBurnTime"+index, lastBurnTime);
		return result;
	}

	public void readNBT(NBTTagCompound compound, int index)
	{
		if(compound==null) return;
		process = compound.getInteger("process"+index);
		processMax = compound.getInteger("processMax"+index);
		burnTime = compound.getInteger("burnTime"+index);
		lastBurnTime = compound.getInteger("lastBurnTime"+index);
	}

	private boolean isAnyInputEmpty(IItemHandler inv)
	{
		for(InputSlot<R> i : inputs)
			if(inv.getStackInSlot(i.slotIndex).isEmpty()) return true;
		return false;
	}

	@Nullable
	private R getRecipe(IFurnaceEnvironment<R> env, int furnaceIndex)
	{
		R recipe = env.getRecipeForInput(furnaceIndex);
		if(recipe==null) return null;

		final IItemHandlerModifiable inv = env.getInventory(furnaceIndex);
		for(OutputSlot<R> out : outputs)
		{
			ItemStack currentStack = inv.getStackInSlot(out.slotIndex);
			ItemStack outputSlot = out.get(recipe);
			if(currentStack.isEmpty()) continue;

			if(currentStack.getItem()!=outputSlot.getItem()
					||currentStack.getMetadata()!=outputSlot.getMetadata())
				return null;
			if(currentStack.getCount()+outputSlot.getCount() > inv.getSlotLimit(out.slotIndex))
				return null;
		}
		return recipe;
	}

	private void doRecipeIO(IFurnaceEnvironment<R> env, int furnaceIndex)
	{
		R recipe = getRecipe(env, furnaceIndex);
		if(recipe==null) return;

		final IItemHandlerModifiable inv = env.getInventory(furnaceIndex);
		for(InputSlot<R> slot : inputs)
		{
			IngredientStack required = slot.get(recipe);
			if(required==null) continue;
			ItemStack inSlot = inv.getStackInSlot(slot.slotIndex);
			if(!inSlot.isEmpty()) inSlot.shrink(Math.max(1, required.inputSize));
		}
		for(OutputSlot<R> slot : outputs)
		{
			ItemStack produced = slot.get(recipe);
			if(produced==null||produced.isEmpty()) continue;

			ItemStack current = inv.getStackInSlot(slot.slotIndex);
			if(current.isEmpty()) inv.setStackInSlot(slot.slotIndex, produced.copy());
			else current.grow(produced.getCount());
		}
		setChanged.run();
	}
}
