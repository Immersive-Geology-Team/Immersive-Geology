package com.igteam.immersivegeology.common.block.multiblocks.structure;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import net.minecraft.block.Block;
import com.igteam.immersivegeology.common.block.multiblocks.IGMultiblockBlock;
import com.igteam.immersivegeology.common.block.multiblocks.shim.IGMultiblockTile;
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
    // 1.12.2 reads things in reverse for .nbt data -_-, had to retake my data and scrap my translation layer;
	// wasn't a good idea anyways would have just caused issues down the line.
	private static void reportMismatch(IGMultiblockStructure structure, IGStructureTemplate template, World world,
									   BlockPos origin, EnumFacing facing)
	{
		int mismatches = 0;
		for(int y = 0; y < template.sizeY()&&mismatches < 4; y++)
			for(int z = 0; z < template.sizeZ()&&mismatches < 4; z++)
				for(int x = 0; x < template.sizeX()&&mismatches < 4; x++)
				{
					String id = template.getBlockId(x, y, z);
					IBlockState expected = template.getBlockState(x, y, z);
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

	private static boolean matches(IGMultiblockStructure structure, IGStructureTemplate template, World world,
								   BlockPos origin, EnumFacing facing)
	{
		for(int y = 0; y < template.sizeY(); y++)
			for(int z = 0; z < template.sizeZ(); z++)
				for(int x = 0; x < template.sizeX(); x++)
				{
					IBlockState expected = template.getBlockState(x, y, z);
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
		IBlockState base = structure.getPartState();
		if(base.getPropertyKeys().contains(IGMultiblockBlock.FACING))
			base = base.withProperty(IGMultiblockBlock.FACING, facing);
		BlockPos master = structure.getMasterOffset();
		BlockPos masterPos = origin.add(rotate(master, facing));

		for(int y = 0; y < template.sizeY(); y++)
			for(int z = 0; z < template.sizeZ(); z++)
				for(int x = 0; x < template.sizeX(); x++)
				{
					IBlockState templateState = template.getBlockState(x, y, z);
					if(templateState==null) continue;

					BlockPos target = origin.add(rotate(new BlockPos(x, y, z), facing));
					IBlockState previous = world.getBlockState(target);
					boolean isMaster = master.getX()==x&&master.getY()==y&&master.getZ()==z;

					IBlockState partState = base.getPropertyKeys().contains(IGMultiblockBlock.DUMMY)
							?base.withProperty(IGMultiblockBlock.DUMMY, !isMaster)
							: base;

					world.setBlockState(target, partState, 3);

					TileEntity te = world.getTileEntity(target);
					if(te instanceof IGMultiblockTile<?> part)
						part.setFormedAt(new int[]{x, y, z},
								IGBlockMapping.toStack(templateState), isMaster, facing, masterPos);

					world.notifyBlockUpdate(target, previous, partState, 3);
				}
	}

	public static BlockPos rotateOffset(BlockPos offset, EnumFacing facing)
	{
		return rotate(offset, facing);
	}

	public static boolean transformsAreProperRotations()
	{
		boolean hasIdentity = false;
		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			BlockPos ux = rotate(new BlockPos(1, 0, 0), facing);
			BlockPos uz = rotate(new BlockPos(0, 0, 1), facing);
			if(ux.getX()*uz.getZ()-uz.getX()*ux.getZ()!=1) return false;
			if(ux.getX()==1&&ux.getZ()==0&&uz.getX()==0&&uz.getZ()==1) hasIdentity = true;
		}
		return hasIdentity;
	}

	private static BlockPos rotate(BlockPos offset, EnumFacing facing)
	{
		EnumFacing width = facing.rotateY();
		int along = -offset.getZ();
		int across = offset.getX();
		return new BlockPos(
				facing.getXOffset()*along+width.getXOffset()*across,
				offset.getY(),
				facing.getZOffset()*along+width.getZOffset()*across);
	}
}
