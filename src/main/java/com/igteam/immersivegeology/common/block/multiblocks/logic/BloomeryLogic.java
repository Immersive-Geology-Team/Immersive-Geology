package com.igteam.immersivegeology.common.block.multiblocks.logic;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryFuel;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.BloomeryRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IInitialMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.MBInventoryUtils;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.SlotwiseItemHandler;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.SlotwiseItemHandler.IOConstraint;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IServerTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler.InputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler.OutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.CachedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class BloomeryLogic implements IMultiblockLogic<BloomeryLogic.State>,
		IServerTickableComponent<BloomeryLogic.State>
{
	public static final int NUM_SLOTS = 4;

	@Override
	public void tickServer(IMultiblockContext<State> context)
	{
		final State state = context.getState();
		boolean active = state.furnace.tickServer(context, 0);
		if(active!=state.renderAsActive)
		{
			state.renderAsActive = active;
			context.requestMasterBESync();
		}
	}

	@Override
	public void dropExtraItems(State state, Consumer<ItemStack> drop)
	{
		MBInventoryUtils.dropItems(state.inventory, drop);
	}

	@Override
	public State createInitialState(IInitialMultiblockContext<State> context)
	{
		return new State(context);
	}

	public static class State implements IMultiblockState, IGFurnaceHandler.IFurnaceEnvironment<BloomeryRecipe>
	{
		private final SlotwiseItemHandler inventory;
		public final IGFurnaceHandler<BloomeryRecipe> furnace;
		private final CachedRecipe<BloomeryRecipe> cachedRecipe;

		public boolean renderAsActive;

		public State(IInitialMultiblockContext<State> ctx)
		{
			this.inventory = new SlotwiseItemHandler(
					Arrays.asList(
							new IOConstraint(true, stack -> BloomeryRecipe.findRecipe(stack)!=null),
							new IOConstraint(true, BloomeryFuel::isValidBloomeryFuel),
							IOConstraint.OUTPUT,
							IOConstraint.OUTPUT),
					ctx.getMarkDirtyRunnable());

			this.furnace = new IGFurnaceHandler<>(
					1,
					List.of(new InputSlot<>(r -> r.input, 0)),
					List.of(new OutputSlot<>(r -> r.result, 2)),
					r -> r.time,
					ctx.getMarkDirtyRunnable());

			this.cachedRecipe = CachedRecipe.cached(BloomeryRecipe::findRecipe,
					() -> this.inventory.getStackInSlot(0));
		}

		public SlotwiseItemHandler getItemHandler()
		{
			return inventory;
		}

		@Override
		public void readSaveNBT(NBTTagCompound nbt)
		{
			inventory.fromNBT(nbt.getTagList("inventory", 10));
			furnace.readNBT(nbt.getCompoundTag("furnace"), 0);
			renderAsActive = nbt.getBoolean("active");
		}

		@Override
		public void writeSaveNBT(NBTTagCompound nbt)
		{
			nbt.setTag("inventory", inventory.toNBT());
			nbt.setTag("furnace", furnace.toNBT(0));
			nbt.setBoolean("active", renderAsActive);
		}

		@Override
		public void readSyncNBT(NBTTagCompound nbt)
		{
			readSaveNBT(nbt);
		}

		@Override
		public void writeSyncNBT(NBTTagCompound nbt)
		{
			writeSaveNBT(nbt);
		}

		@Override
		public IItemHandlerModifiable getInventory(int furnaceIndex)
		{
			return inventory;
		}

		@Nullable
		@Override
		public BloomeryRecipe getRecipeForInput(int furnaceIndex)
		{
			return cachedRecipe.get();
		}

		@Override
		public int getBurnTimeOf(World world, ItemStack fuel)
		{
			return BloomeryFuel.getBloomeryFuelTime(fuel);
		}
	}
}
