package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.CapabilityPosition;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IMultiblockComponent<State>
{
	@Nullable
	default <T> T getCapability(IMultiblockContext<State> context, CapabilityPosition position, Capability<T> capability)
	{
		return null;
	}

	default void dropExtraItems(State state, Consumer<ItemStack> drop)
	{
	}
}
