package com.igteam.immersivegeology.common.block.multiblocks.structure;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class IGStructureFormer
{
	private IGStructureFormer()
	{
	}

	public static boolean tryForm(IGMultiblockStructure structure, World world, BlockPos clicked, EntityPlayer player)
	{
		IGStructureTemplate template = structure.getTemplate();
		if(template==null) return false;

		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			BlockPos origin = findOrigin(structure, clicked, facing);
			if(matches(structure, template, world, origin, facing))
			{
				place(structure, template, world, origin, facing);
				return true;
			}
		}
		return false;
	}

	private static BlockPos findOrigin(IGMultiblockStructure structure, BlockPos clicked, EnumFacing facing)
	{
		BlockPos trigger = structure.getTriggerOffset();
		return clicked.subtract(rotate(trigger, facing));
	}

	private static boolean matches(IGMultiblockStructure structure, IGStructureTemplate template, World world,
								   BlockPos origin, EnumFacing facing)
	{
		for(int y = 0; y < template.sizeY(); y++)
			for(int z = 0; z < template.sizeZ(); z++)
				for(int x = 0; x < template.sizeX(); x++)
				{
					ItemStack expected = IGBlockMapping.toStack(template.getBlockId(x, y, z));
					BlockPos target = origin.add(rotate(new BlockPos(x, y, z), facing));
					IBlockState actual = world.getBlockState(target);

					if(expected.isEmpty())
					{
						if(!actual.getBlock().isAir(actual, world, target)) return false;
						continue;
					}

					Block expectedBlock = Block.getBlockFromItem(expected.getItem());
					if(expectedBlock==Blocks.AIR) continue;
					if(actual.getBlock()!=expectedBlock) return false;
					if(expected.getMetadata()!=expectedBlock.getMetaFromState(actual)) return false;
				}
		return true;
	}

	private static void place(IGMultiblockStructure structure, IGStructureTemplate template, World world,
							  BlockPos origin, EnumFacing facing)
	{
		IBlockState partState = structure.getPartState();

		for(int y = 0; y < template.sizeY(); y++)
			for(int z = 0; z < template.sizeZ(); z++)
				for(int x = 0; x < template.sizeX(); x++)
				{
					BlockPos target = origin.add(rotate(new BlockPos(x, y, z), facing));
					IBlockState previous = world.getBlockState(target);
					ItemStack original = IGBlockMapping.toStack(template.getBlockId(x, y, z));

					world.setBlockState(target, partState, 3);

					TileEntity te = world.getTileEntity(target);
					if(te instanceof TileEntityMultiblockPart<?> part)
						part.replaceStructureBlock(target, previous, original, y, z, x);

					world.notifyBlockUpdate(target, previous, partState, 3);
				}
	}

	private static BlockPos rotate(BlockPos offset, EnumFacing facing)
	{
		return switch(facing)
		{
			case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
			case WEST -> new BlockPos(-offset.getZ(), offset.getY(), -offset.getX());
			case EAST -> new BlockPos(offset.getZ(), offset.getY(), offset.getX());
			default -> new BlockPos(offset.getX(), offset.getY(), -offset.getZ());
		};
	}
}
