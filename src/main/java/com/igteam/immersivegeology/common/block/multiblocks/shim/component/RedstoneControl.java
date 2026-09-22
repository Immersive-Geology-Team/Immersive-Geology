package com.igteam.immersivegeology.common.block.multiblocks.shim.component;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;

public final class RedstoneControl
{
	private RedstoneControl()
	{
	}

	public static class RSState
	{
		private boolean inverted;
		private final BlockPos inputPosition;

		private RSState(boolean inverted, BlockPos inputPosition)
		{
			this.inverted = inverted;
			this.inputPosition = inputPosition;
		}

		public static RSState enabledByDefault()
		{
			return new RSState(true, null);
		}

		public static RSState disabledByDefault()
		{
			return new RSState(false, null);
		}

		public static RSState enabledByDefault(BlockPos inputPosition)
		{
			return new RSState(true, inputPosition);
		}

		public boolean isInverted()
		{
			return inverted;
		}

		public void setInverted(boolean inverted)
		{
			this.inverted = inverted;
		}

		public void toggleInverted()
		{
			this.inverted = !this.inverted;
		}

		public boolean isEnabled(IMultiblockContext<?> context)
		{
			if(inputPosition==null) return inverted;
			int signal = context.getRedstoneInputValue(inputPosition, 0);
			return inverted==(signal==0);
		}

		public void writeNBT(NBTTagCompound nbt)
		{
			nbt.setBoolean("rsInverted", inverted);
		}

		public void readNBT(NBTTagCompound nbt)
		{
			this.inverted = nbt.getBoolean("rsInverted");
		}
	}
}
