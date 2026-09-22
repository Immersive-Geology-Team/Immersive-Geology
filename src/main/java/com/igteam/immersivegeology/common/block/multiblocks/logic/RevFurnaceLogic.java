package com.igteam.immersivegeology.common.block.multiblocks.logic;

import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.shim.component.IServerTickableComponent;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IInitialMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.MBInventoryUtils;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.SlotwiseItemHandler;
import com.igteam.immersivegeology.common.block.multiblocks.shim.inventory.SlotwiseItemHandler.IOConstraint;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockLogic;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.IMultiblockState;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler.InputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.shim.logic.helper.IGFurnaceHandler.OutputSlot;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.CachedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RevFurnaceLogic implements IMultiblockLogic<RevFurnaceLogic.State>,
		IServerTickableComponent<RevFurnaceLogic.State>
{
	public static final int NUM_SLOTS = 6;

	@Override
	public void tickServer(IMultiblockContext<State> context)
	{
		final State state = context.getState();

		boolean left = state.furnaceLeft.tickServer(context, 0);
		boolean right = state.furnaceRight.tickServer(context, 1);
		boolean active = left||right;

		state.activeLeft = left;
		state.activeRight = right;

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

	public static class State implements IMultiblockState, IGFurnaceHandler.IFurnaceEnvironment<RevFurnaceRecipe>
	{
		private final SlotwiseItemHandler inventory;

		public final IGFurnaceHandler<RevFurnaceRecipe> furnaceLeft;
		public final IGFurnaceHandler<RevFurnaceRecipe> furnaceRight;

		private final CachedRecipe<RevFurnaceRecipe> cachedLeft;
		private final CachedRecipe<RevFurnaceRecipe> cachedRight;

		public boolean activeLeft;
		public boolean activeRight;
		public boolean renderAsActive;

		public State(IInitialMultiblockContext<State> ctx)
		{
			IOConstraint roastable = new IOConstraint(true, stack -> RevFurnaceRecipe.findRecipe(stack)!=null);
			IOConstraint fuel = new IOConstraint(true, State::isValidFuel);

			this.inventory = new SlotwiseItemHandler(
					Arrays.asList(roastable, fuel, IOConstraint.OUTPUT,
							roastable, fuel, IOConstraint.OUTPUT),
					ctx.getMarkDirtyRunnable());

			this.furnaceLeft = new IGFurnaceHandler<>(
					1,
					List.of(new InputSlot<>(r -> r.input, 0)),
					List.of(new OutputSlot<>(r -> r.result, 2)),
					r -> r.time,
					ctx.getMarkDirtyRunnable());

			this.furnaceRight = new IGFurnaceHandler<>(
					4,
					List.of(new InputSlot<>(r -> r.input, 3)),
					List.of(new OutputSlot<>(r -> r.result, 5)),
					r -> r.time,
					ctx.getMarkDirtyRunnable());

			this.cachedLeft = CachedRecipe.cached(RevFurnaceRecipe::findRecipe,
					() -> this.inventory.getStackInSlot(0));
			this.cachedRight = CachedRecipe.cached(RevFurnaceRecipe::findRecipe,
					() -> this.inventory.getStackInSlot(3));
		}

		private static boolean isValidFuel(ItemStack stack)
		{
			return blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe
					.isValidBlastFuel(stack);
		}

		public SlotwiseItemHandler getItemHandler()
		{
			return inventory;
		}

		@Override
		public IItemHandlerModifiable getInventory(int furnaceIndex)
		{
			return furnaceIndex==0
					?new RangedWrapper(inventory, 0, 3)
					: new RangedWrapper(inventory, 3, 6);
		}

		@Nullable
		@Override
		public RevFurnaceRecipe getRecipeForInput(int furnaceIndex)
		{
			return furnaceIndex==0?cachedLeft.get(): cachedRight.get();
		}

		@Override
		public int getBurnTimeOf(World world, ItemStack fuel)
		{
			return blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe
					.getBlastFuelTime(fuel);
		}

		@Override
		public void readSaveNBT(NBTTagCompound nbt)
		{
			inventory.fromNBT(nbt.getTagList("inventory", 10));
			furnaceLeft.readNBT(nbt.getCompoundTag("furnaceLeft"), 0);
			furnaceRight.readNBT(nbt.getCompoundTag("furnaceRight"), 1);
			activeLeft = nbt.getBoolean("activeLeft");
			activeRight = nbt.getBoolean("activeRight");
			renderAsActive = nbt.getBoolean("active");
		}

		@Override
		public void writeSaveNBT(NBTTagCompound nbt)
		{
			nbt.setTag("inventory", inventory.toNBT());
			nbt.setTag("furnaceLeft", furnaceLeft.toNBT(0));
			nbt.setTag("furnaceRight", furnaceRight.toNBT(1));
			nbt.setBoolean("activeLeft", activeLeft);
			nbt.setBoolean("activeRight", activeRight);
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
	}
}
