package com.igteam.immersivegeology.common.gui;

import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBloomery;
import com.igteam.immersivegeology.common.block.multiblocks.shim.IGMultiblockTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class IGGuiHandler implements IGuiHandler
{
	public static final int BLOOMERY = 0;
	public static final int REVERBERATION_FURNACE = 1;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = masterAt(world, new BlockPos(x, y, z));
		if(id==BLOOMERY&&te instanceof TileEntityBloomery bloomery)
			return new ContainerBloomery(player.inventory, bloomery);
		if(id==REVERBERATION_FURNACE&&te instanceof com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityRevFurnace furnace)
			return new ContainerRevFurnace(player.inventory, furnace);
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = masterAt(world, new BlockPos(x, y, z));
		if(id==BLOOMERY&&te instanceof TileEntityBloomery bloomery)
			return new com.igteam.immersivegeology.client.gui.GuiBloomery(
					player.inventory, new ContainerBloomery(player.inventory, bloomery));
		if(id==REVERBERATION_FURNACE&&te instanceof com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityRevFurnace furnace)
			return new com.igteam.immersivegeology.client.gui.GuiRevFurnace(
					player.inventory, new ContainerRevFurnace(player.inventory, furnace));
		return null;
	}

	private static TileEntity masterAt(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IGMultiblockTile<?> part)
		{
			IGMultiblockTile<?> master = part.master();
			if(master!=null) return master;
		}
		return te;
	}
}
