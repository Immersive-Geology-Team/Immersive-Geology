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

		if(DEBUG_FORMATION)
			for(EnumFacing facing : EnumFacing.HORIZONTALS)
				reportMismatch(structure, template, world, findOrigin(structure, clicked, facing), facing);

		return false;
	}

	private static final boolean DEBUG_FORMATION = Boolean.getBoolean("ig.debugMultiblockFormation");
    // I think I have this backwards... I might be reading things backwards...
	private static void reportMismatch(IGMultiblockStructure structure, IGStructureTemplate template, World world,
									   BlockPos origin, EnumFacing facing)
	{
		int mismatches = 0;
		for(int y = 0; y < template.sizeY()&&mismatches < 4; y++)
			for(int z = 0; z < template.sizeZ()&&mismatches < 4; z++)
				for(int x = 0; x < template.sizeX()&&mismatches < 4; x++)
				{
					String id = template.getBlockId(x, y, z);
					IBlockState expected = IGBlockMapping.toState(id);
					BlockPos target = origin.add(rotate(new BlockPos(x, y, z), facing));
					IBlockState actual = world.getBlockState(target);

					boolean ok = expected==null
							?actual.getBlock().isAir(actual, world, target)
							: actual.getBlock()==expected.getBlock()
							&&actual.getBlock().getMetaFromState(actual)
							==expected.getBlock().getMetaFromState(expected);

					if(!ok)
					{
						com.igteam.immersivegeology.core.lib.IGLib.IG_LOGGER.warn(
								"[{}] facing {} at template ({},{},{}) world {}: wanted {} ({}), found {}",
								structure.getUniqueName(), facing, x, y, z, target, id, expected, actual);
						mismatches++;
					}
				}
	}

	private static BlockPos findOrigin(IGMultiblockStructure structure, BlockPos clicked, EnumFacing facing)
	{
		BlockPos trigger = structure.getTriggerOffset();
		return clicked.subtract(rotate(trigger, facing));
	}

	// I'll need to recheck this once I've had full sleep.
	private static boolean matches(IGMultiblockStructure structure, IGStructureTemplate template, World world,
								   BlockPos origin, EnumFacing facing)
	{
		for(int y = 0; y < template.sizeY(); y++)
			for(int z = 0; z < template.sizeZ(); z++)
				for(int x = 0; x < template.sizeX(); x++)
				{
					IBlockState expected = IGBlockMapping.toState(template.getBlockId(x, y, z));
					BlockPos target = origin.add(rotate(new BlockPos(x, y, z), facing));
					IBlockState actual = world.getBlockState(target);

					if(expected==null)
					{
						if(!actual.getBlock().isAir(actual, world, target)) return false;
						continue;
					}

					if(actual.getBlock()!=expected.getBlock()) return false;
					if(actual.getBlock().getMetaFromState(actual)
							!=expected.getBlock().getMetaFromState(expected)) return false;
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
					if(IGBlockMapping.toState(template.getBlockId(x, y, z))==null) continue;

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
