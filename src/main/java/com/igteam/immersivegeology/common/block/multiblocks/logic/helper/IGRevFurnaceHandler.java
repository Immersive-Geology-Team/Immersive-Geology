/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is partially licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the Immersive Engineering Github
 * This class is a modified version of the FurnaceHandler found in the Immersive Engineering Github.
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGRevFurnaceHandler.RevStateView.CURRENT_PROCESS_LEFT;

public class IGRevFurnaceHandler<R extends IESerializableRecipe>
{
	// Rev Furnace Variables
	private int processLeft = 0;
	private int processMaxLeft = 0;
	private int burnTimeLeft = 0;
	private int lastBurnTimeLeft = 0;

	private int processRight = 0;
	private int processMaxRight = 0;
	private int burnTimeRight = 0;
	private int lastBurnTimeRight = 0;
	public final RevStateView stateView = new RevStateView();

	private final int fuelSlotLeft;
	private final int fuelSlotRight;

	private final List<RevInputSlot<R>> inputsLeft;
	private final List<RevOutputSlot<R>> outputsLeft;
	private final ToIntFunction<R> getProcessingTimeLeft;

	private final List<RevInputSlot<R>> inputsRight;
	private final List<RevOutputSlot<R>> outputsRight;
	private final ToIntFunction<R> getProcessingTimeRight;
	private final Runnable setChanged;

	public IGRevFurnaceHandler(
			int fuelSlotLeft,
			List<RevInputSlot<R>> inputsLeft,
			List<RevOutputSlot<R>> outputsLeft,
			ToIntFunction<R> getProcessingTimeLeft,
			int fuelSlotRight,
			List<RevInputSlot<R>> inputsRight,
			List<RevOutputSlot<R>> outputsRight,
			ToIntFunction<R> getProcessingTimeRight,
			Runnable setChanged
	)
	{
		this.fuelSlotLeft = fuelSlotLeft;
		this.inputsLeft = inputsLeft;
		this.outputsLeft = outputsLeft;
		this.getProcessingTimeLeft = getProcessingTimeLeft;

		this.fuelSlotRight = fuelSlotRight;
		this.inputsRight = inputsRight;
		this.outputsRight = outputsRight;
		this.getProcessingTimeRight = getProcessingTimeRight;
		this.setChanged = setChanged;
	}

	public boolean tickServerLeft(IMultiblockContext<? extends IGRevFurnaceHandler.IRevFurnaceEnvironment<R>> ctx)
	{
		boolean active = false;
		final IGRevFurnaceHandler.IRevFurnaceEnvironment<R> env = ctx.getState();

		if(burnTimeLeft > 0)
		{
			int processSpeed = 1;
			if(processLeft > 0)
				processSpeed = env.getProcessSpeed(ctx.getLevel());
			burnTimeLeft -= processSpeed;
			if(processLeft > 0)
			{
				if(isAnyInputEmpty(env.getInventory(), true))
				{
					processLeft = 0;
					processMaxLeft = 0;
				}
				else
				{
					R recipe = getRecipeLeft(env);
					if(recipe!=null&&getProcessTimeLeft(recipe)!=processMaxLeft)
					{
						processMaxLeft = 0;
						processLeft = 0;
					}
					else
					{
						processLeft -= processSpeed;
						processSpeed = 0;//Process speed is "used up"
						active = true;
					}
				}
				setChanged.run();
			}

			if(processLeft <= 0)
			{
				if(processMaxLeft > 0)
				{
					doRecipeIOLeft(env);
					processMaxLeft = 0;
					burnTimeLeft -= processLeft;
				}
				R recipe = getRecipeLeft(env);
				if(recipe!=null)
				{
					final int time = getProcessTimeLeft(recipe);
					this.processLeft = time-processSpeed;
					this.processMaxLeft = time;
					active = true;
				}
			}
		}

		if(burnTimeLeft <= 0&&getRecipeLeft(env)!=null)
		{
			final IItemHandlerModifiable inv = env.getInventory();
			final ItemStack fuel = inv.getStackInSlot(fuelSlotLeft);
			final int addedBurntime = env.getBurnTimeOf(ctx.getLevel().getRawLevel(), fuel);
			if(addedBurntime > 0)
			{
				lastBurnTimeLeft = addedBurntime;
				burnTimeLeft += lastBurnTimeLeft;
				if(fuel.hasCraftingRemainingItem()&&fuel.getCount()==1)
					inv.setStackInSlot(fuelSlotLeft, fuel.getCraftingRemainingItem());
				else
					fuel.shrink(1);
				setChanged.run();
			}
		}

		if(!active)
			env.turnOff(ctx.getLevel(), true);
		return active;
	}

	public boolean tickServerRight(IMultiblockContext<? extends IGRevFurnaceHandler.IRevFurnaceEnvironment<R>> ctx)
	{
		boolean active = false;
		final IGRevFurnaceHandler.IRevFurnaceEnvironment<R> env = ctx.getState();

		if(burnTimeRight > 0)
		{
			int processSpeed = 1;
			if(processRight > 0)
				processSpeed = env.getProcessSpeed(ctx.getLevel());
			burnTimeRight -= processSpeed;
			if(processRight > 0)
			{
				if(isAnyInputEmpty(env.getInventory(), false))
				{
					processRight = 0;
					processMaxRight = 0;
				}
				else
				{
					R recipe = getRecipeRight(env);
					if(recipe!=null&&getProcessTimeRight(recipe)!=processMaxRight)
					{
						processMaxRight = 0;
						processRight = 0;
					}
					else
					{
						processRight -= processSpeed;
						processSpeed = 0;//Process speed is "used up"
						active = true;
					}
				}
				setChanged.run();
			}

			if(processRight <= 0)
			{
				if(processMaxRight > 0)
				{
					doRecipeIORight(env);
					processMaxRight = 0;
					burnTimeRight -= processRight;
				}
				R recipe = getRecipeRight(env);
				if(recipe!=null)
				{
					final int time = getProcessTimeRight(recipe);
					this.processRight = time-processSpeed;
					this.processMaxRight = time;
					active = true;
				}
			}
		}

		if(burnTimeRight <= 0&&getRecipeRight(env)!=null)
		{
			final IItemHandlerModifiable inv = env.getInventory();
			final ItemStack fuel = inv.getStackInSlot(fuelSlotRight);
			final int addedBurntime = env.getBurnTimeOf(ctx.getLevel().getRawLevel(), fuel);
			if(addedBurntime > 0)
			{
				lastBurnTimeRight = addedBurntime;
				burnTimeRight += lastBurnTimeRight;
				if(fuel.hasCraftingRemainingItem()&&fuel.getCount()==1)
					inv.setStackInSlot(fuelSlotRight, fuel.getCraftingRemainingItem());
				else
					fuel.shrink(1);
				setChanged.run();
			}
		}

		if(!active)
			env.turnOff(ctx.getLevel(), false);
		return active;
	}

	public Tag toNBT()
	{
		final CompoundTag result = new CompoundTag();
		result.putInt("processLeft", processLeft);
		result.putInt("processMaxLeft", processMaxLeft);
		result.putInt("burnTimeLeft", burnTimeLeft);
		result.putInt("lastBurnTimeLeft", lastBurnTimeLeft);

		result.putInt("processRight", processRight);
		result.putInt("processMaxRight", processMaxRight);
		result.putInt("burnTimeRight", burnTimeRight);
		result.putInt("lastBurnTimeRight", lastBurnTimeRight);
		return result;
	}

	public void readNBT(Tag nbt)
	{
		if(!(nbt instanceof CompoundTag compound))
			return;
		processLeft = compound.getInt("processLeft");
		processMaxLeft = compound.getInt("processMaxLeft");
		burnTimeLeft = compound.getInt("burnTimeLeft");
		lastBurnTimeLeft = compound.getInt("lastBurnTimeLeft");

		processRight = compound.getInt("processRight");
		processMaxRight = compound.getInt("processMaxRight");
		burnTimeRight = compound.getInt("burnTimeRight");
		lastBurnTimeRight = compound.getInt("lastBurnTimeRight");
	}

	private boolean isAnyInputEmpty(IItemHandler inv, boolean isLeft)
	{
		for(RevInputSlot<R> i : (isLeft ? inputsLeft : inputsRight))
			if(inv.getStackInSlot(i.slotIndex).isEmpty())
				return true;
		return false;
	}

	@Nullable
	private R getRecipeLeft(IRevFurnaceEnvironment<R> env)
	{
		R recipe = env.getRecipeForInput(true);
		if(recipe==null)
			return null;
		final IItemHandlerModifiable inv = env.getInventory();
		for(RevOutputSlot<R> out : outputsLeft)
		{
			ItemStack currentStack = inv.getStackInSlot(out.slotIndex);
			ItemStack outputSlot = out.get(recipe);
			if(!currentStack.isEmpty())
			{
				if(!ItemStack.isSameItem(currentStack, outputSlot))
					return null;
				else if(currentStack.getCount()+outputSlot.getCount() > inv.getSlotLimit(out.slotIndex))
					return null;
			}
		}
		return recipe;
	}

	@Nullable
	private R getRecipeRight(IRevFurnaceEnvironment<R> env)
	{
		R recipe = env.getRecipeForInput(false);
		if(recipe==null)
			return null;
		final IItemHandlerModifiable inv = env.getInventory();
		for(RevOutputSlot<R> out : outputsRight)
		{
			ItemStack currentStack = inv.getStackInSlot(out.slotIndex);
			ItemStack outputSlot = out.get(recipe);
			if(!currentStack.isEmpty())
			{
				if(!ItemStack.isSameItem(currentStack, outputSlot))
					return null;
				else if(currentStack.getCount()+outputSlot.getCount() > inv.getSlotLimit(out.slotIndex))
					return null;
			}
		}
		return recipe;
	}

	private void doRecipeIOLeft(IRevFurnaceEnvironment<R> env)
	{
		R recipe = getRecipeLeft(env);
		if(recipe==null)
			return;
		final IItemHandlerModifiable inv = env.getInventory();
		for(RevInputSlot<R> slot : inputsLeft)
		{
			int reqSize = inputsLeft.stream()
					.map(matchSlot -> matchSlot.get(recipe))
					.filter(ingr -> ingr.test(inv.getStackInSlot(slot.slotIndex)))
					.mapToInt(IngredientWithSize::getCount).findFirst().orElse(0);
			inv.getStackInSlot(slot.slotIndex).shrink(reqSize);
		}

		for(RevOutputSlot<R> slot : outputsLeft)
		{
			ItemStack result = slot.get(recipe);
			if(!result.isEmpty())
			{
				if(!inv.getStackInSlot(slot.slotIndex).isEmpty())
					inv.getStackInSlot(slot.slotIndex).grow(result.getCount());
				else
					inv.setStackInSlot(slot.slotIndex, result.copy());
			}
		}
		if(recipe instanceof RevFurnaceRecipe revRecipe)
		{
			if(env instanceof RevFurnaceLogic.State state)
			{
				state.addToTank(revRecipe.getWasteAmount());
			}
		}
	}

	private void doRecipeIORight(IRevFurnaceEnvironment<R> env)
	{
		R recipe = getRecipeRight(env);
		if(recipe==null)
			return;
		final IItemHandlerModifiable inv = env.getInventory();
		for(RevInputSlot<R> slot : inputsRight)
		{
			int reqSize = inputsRight.stream()
					.map(matchSlot -> matchSlot.get(recipe))
					.filter(ingr -> ingr.test(inv.getStackInSlot(slot.slotIndex)))
					.mapToInt(IngredientWithSize::getCount).findFirst().orElse(0);
			inv.getStackInSlot(slot.slotIndex).shrink(reqSize);
		}

		for(RevOutputSlot<R> slot : outputsRight)
		{
			ItemStack result = slot.get(recipe);
			if(!result.isEmpty())
			{
				if(!inv.getStackInSlot(slot.slotIndex).isEmpty())
					inv.getStackInSlot(slot.slotIndex).grow(result.getCount());
				else
					inv.setStackInSlot(slot.slotIndex, result.copy());
			}
		}
		if(recipe instanceof RevFurnaceRecipe revRecipe)
		{
			if(env instanceof RevFurnaceLogic.State state)
			{
				state.addToTank(revRecipe.getWasteAmount());
			}
		}
	}

	private int getProcessTimeLeft(R recipe)
	{
		return getProcessingTimeLeft.applyAsInt(recipe);
	}

	private int getProcessTimeRight(R recipe)
	{
		return getProcessingTimeRight.applyAsInt(recipe);
	}

	public interface IRevFurnaceEnvironment<R extends IESerializableRecipe>
	{
		IItemHandlerModifiable getInventory();

		@Nullable
		R getRecipeForInput(boolean isLeft);

		int getBurnTimeOf(Level level, ItemStack fuel);

		default int getProcessSpeed(IMultiblockLevel level)
		{
			return 1;
		}

		default void turnOff(IMultiblockLevel level, boolean isLeft)
		{
		}
	}

	public class RevStateView implements ContainerData
	{
		public static final int LAST_BURN_TIME_LEFT = 0;
		public static final int BURN_TIME_LEFT = 1;
		public static final int PROCESS_MAX_LEFT = 2;
		public static final int CURRENT_PROCESS_LEFT = 3;

		public static final int LAST_BURN_TIME_RIGHT = 4;
		public static final int BURN_TIME_RIGHT = 5;
		public static final int PROCESS_MAX_RIGHT = 6;
		public static final int CURRENT_PROCESS_RIGHT = 7;

		public static final int NUM_SLOTS = 8;

		public static int getLastBurnTimeLeft(ContainerData data)
		{
			return data.get(LAST_BURN_TIME_LEFT);
		}

		public static int getBurnTimeLeft(ContainerData data)
		{
			return data.get(BURN_TIME_LEFT);
		}

		public static int getMaxProcessLeft(ContainerData data)
		{
			return data.get(PROCESS_MAX_LEFT);
		}

		public static int getProcessLeft(ContainerData data)
		{
			return data.get(CURRENT_PROCESS_LEFT);
		}

		public static int getLastBurnTimeRight(ContainerData data)
		{
			return data.get(LAST_BURN_TIME_RIGHT);
		}

		public static int getBurnTimeRight(ContainerData data)
		{
			return data.get(BURN_TIME_RIGHT);
		}

		public static int getMaxProcessRight(ContainerData data)
		{
			return data.get(PROCESS_MAX_RIGHT);
		}

		public static int getProcessRight(ContainerData data)
		{
			return data.get(CURRENT_PROCESS_RIGHT);
		}

		@Override
		public int get(int index)
		{
			return switch(index)
			{
				case LAST_BURN_TIME_LEFT -> lastBurnTimeLeft;
				case BURN_TIME_LEFT -> burnTimeLeft;
				case PROCESS_MAX_LEFT -> processMaxLeft;
				case CURRENT_PROCESS_LEFT -> processLeft;
				case LAST_BURN_TIME_RIGHT -> lastBurnTimeRight;
				case BURN_TIME_RIGHT -> burnTimeRight;
				case PROCESS_MAX_RIGHT -> processMaxRight;
				case CURRENT_PROCESS_RIGHT -> processRight;
				default -> throw new IllegalArgumentException("Unknown index "+index);
			};
		}

		@Override
		public void set(int index, int value)
		{
			switch(index)
			{
				case LAST_BURN_TIME_LEFT:
					lastBurnTimeLeft = value;
					break;
				case BURN_TIME_LEFT:
					burnTimeLeft = value;
					break;
				case PROCESS_MAX_LEFT:
					processMaxLeft = value;
					break;
				case CURRENT_PROCESS_LEFT:
					processLeft = value;
					break;
				case LAST_BURN_TIME_RIGHT:
					lastBurnTimeRight = value;
					break;
				case BURN_TIME_RIGHT:
					burnTimeRight = value;
					break;
				case PROCESS_MAX_RIGHT:
					processMaxRight = value;
					break;
				case CURRENT_PROCESS_RIGHT:
					processRight = value;
					break;
				default:
					throw new IllegalArgumentException("Unknown index "+index);
			}
		}

		@Override
		public int getCount()
		{
			return NUM_SLOTS;
		}
	}

	public static class RevInputSlot<R>
	{
		private final Function<R, IngredientWithSize> getFromRecipe;
		private final int slotIndex;

		public RevInputSlot(Function<R, IngredientWithSize> getFromRecipe, int slotIndex)
		{
			this.getFromRecipe = getFromRecipe;
			this.slotIndex = slotIndex;
		}

		public IngredientWithSize get(R recipe)
		{
			return getFromRecipe.apply(recipe);
		}
	}

	public static class RevOutputSlot<R>
	{
		private final Function<R, Lazy<ItemStack>> getFromRecipe;
		private final int slotIndex;

		public RevOutputSlot(Function<R, Lazy<ItemStack>> getFromRecipe, int slotIndex)
		{
			this.getFromRecipe = getFromRecipe;
			this.slotIndex = slotIndex;
		}

		public ItemStack get(R recipe)
		{
			return getFromRecipe.apply(recipe).get();
		}
	}
}
