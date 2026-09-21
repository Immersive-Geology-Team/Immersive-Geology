package com.igteam.immersivegeology.common.block.multiblocks.shim.inventory;

import com.igteam.immersivegeology.common.block.multiblocks.shim.env.IMultiblockLevel;
import com.igteam.immersivegeology.common.block.multiblocks.shim.util.MultiblockFace;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
		EnumFacing facing = level.toAbsolute(outputFace.face());
		BlockPos origin = level.toAbsolute(outputFace.posInMultiblock());
		BlockPos target = origin.offset(facing);

		ItemStack remaining = stack.copy();
		TileEntity neighbour = world.getTileEntity(target);
		if(neighbour!=null&&neighbour.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
		{
			IItemHandler handler = neighbour.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					facing.getOpposite());
			if(handler!=null) remaining = ItemHandlerHelper.insertItem(handler, remaining, false);
		}

		if(remaining.isEmpty()||world.isRemote) return;

		EntityItem entity = new EntityItem(world,
				target.getX()+0.5, target.getY()+0.5, target.getZ()+0.5, remaining);
		entity.motionX = facing.getXOffset()*0.1;
		entity.motionY = 0.05;
		entity.motionZ = facing.getZOffset()*0.1;
		world.spawnEntity(entity);
	}
}
