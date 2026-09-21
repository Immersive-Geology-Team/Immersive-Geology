package com.igteam.immersivegeology.common.block.multiblocks.shim.env;

import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockOrientation;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public interface IMultiblockLevel
{
	World getRawLevel();

	IBlockState getBlockState(BlockPos posInMultiblock);

	void setBlock(BlockPos posInMultiblock, IBlockState state);

	@Nullable
	TileEntity getBlockEntity(BlockPos posInMultiblock);

	@Nullable
	<T> T getCapabilityValue(Capability<T> capability, BlockPos posInMultiblock, @Nullable RelativeBlockFace side);

	boolean shouldTickModulo(int modulo);

	BlockPos getAbsoluteOrigin();

	MultiblockOrientation getOrientation();

	BlockPos toAbsolute(BlockPos posInMultiblock);

	EnumFacing toAbsolute(RelativeBlockFace face);

	AxisAlignedBB toAbsolute(AxisAlignedBB box);
}
