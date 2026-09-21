package com.igteam.immersivegeology.common.block.multiblocks.shim.util;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public record CapabilityPosition(BlockPos posInMultiblock, @Nullable RelativeBlockFace side)
{
	public CapabilityPosition(int x, int y, int z, @Nullable RelativeBlockFace side)
	{
		this(new BlockPos(x, y, z), side);
	}

	public static CapabilityPosition opposing(MultiblockFace face)
	{
		return new CapabilityPosition(face.posInMultiblock(), face.face().getOpposite());
	}

	public boolean equalsOrNullFace(CapabilityPosition other)
	{
		if(other==null) return false;
		if(!posInMultiblock.equals(other.posInMultiblock())) return false;
		return other.side()==null||side==null||side==other.side();
	}
}
