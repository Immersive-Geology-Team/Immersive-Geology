package com.igteam.immersivegeology.common.block.multiblocks.shim.inventory;

import blusunrize.immersiveengineering.common.util.Utils;
import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockFace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DroppingMultiblockOutput
{
	private final MultiblockFace outputFace;

	public DroppingMultiblockOutput(MultiblockFace outputFace)
	{
		this.outputFace = outputFace;
	}

	public void insertOrDrop(ItemStack stack, IMultiblockLevel level)
	{
		if(stack.isEmpty()||level==null) return;

		World world = level.getRawLevel();
		if(world==null||world.isRemote) return;

		EnumFacing face = level.toAbsolute(outputFace.face());
		BlockPos dropPos = level.toAbsolute(outputFace.posInMultiblock());

		ItemStack remaining = stack.copy();
		TileEntity neighbour = world.getTileEntity(dropPos);
		if(neighbour!=null)
		{
			remaining = Utils.insertStackIntoInventory(neighbour, remaining, face);
			if(remaining==null||remaining.isEmpty()) return;
		}

		Utils.dropStackAtPos(world, dropPos, remaining, face.getOpposite());
	}
}
