package com.igteam.immersivegeology.common.block.multiblocks.shim.env;

import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockOrientation;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.RelativeBlockFace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGStructureFormer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class IGMultiblockLevel implements IMultiblockLevel
{
	private final World world;
	private final BlockPos origin;
	private final MultiblockOrientation orientation;

	public IGMultiblockLevel(World world, BlockPos origin, MultiblockOrientation orientation)
	{
		this.world = world;
		this.origin = origin;
		this.orientation = orientation;
	}

	@Override
	public World getRawLevel()
	{
		return world;
	}

	@Override
	public IBlockState getBlockState(BlockPos posInMultiblock)
	{
		return world.getBlockState(toAbsolute(posInMultiblock));
	}

	@Override
	public void setBlock(BlockPos posInMultiblock, IBlockState state)
	{
		world.setBlockState(toAbsolute(posInMultiblock), state);
	}

	@Override
	@Nullable
	public TileEntity getBlockEntity(BlockPos posInMultiblock)
	{
		return world.getTileEntity(toAbsolute(posInMultiblock));
	}

	@Override
	@Nullable
	public <T> T getCapabilityValue(Capability<T> capability, BlockPos posInMultiblock, @Nullable RelativeBlockFace side)
	{
		TileEntity te = getBlockEntity(posInMultiblock);
		if(te==null) return null;
		EnumFacing facing = side==null?null: toAbsolute(side);
		return te.hasCapability(capability, facing)?te.getCapability(capability, facing): null;
	}

	@Override
	public boolean shouldTickModulo(int modulo)
	{
		return world.getTotalWorldTime()%modulo==0;
	}

	@Override
	public BlockPos getAbsoluteOrigin()
	{
		return origin;
	}

	@Override
	public MultiblockOrientation getOrientation()
	{
		return orientation;
	}

	@Override
	public BlockPos toAbsolute(BlockPos posInMultiblock)
	{
		BlockPos local = orientation.mirrored()
				?new BlockPos(-posInMultiblock.getX(), posInMultiblock.getY(), posInMultiblock.getZ())
				: posInMultiblock;
		return origin.add(IGStructureFormer.rotateOffset(local, orientation.front()));
	}

	@Override
	public EnumFacing toAbsolute(RelativeBlockFace face)
	{
		return face.forFront(orientation);
	}

	@Override
	public AxisAlignedBB toAbsolute(AxisAlignedBB box)
	{
		BlockPos min = toAbsolute(new BlockPos(box.minX, box.minY, box.minZ));
		BlockPos max = toAbsolute(new BlockPos(box.maxX, box.maxY, box.maxZ));
		return new AxisAlignedBB(
				Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()),
				Math.max(min.getX(), max.getX())+1, Math.max(min.getY(), max.getY())+1, Math.max(min.getZ(), max.getZ())+1);
	}
}
