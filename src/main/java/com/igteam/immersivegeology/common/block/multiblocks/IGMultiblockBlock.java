package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGMultiblockStructure;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class IGMultiblockBlock extends Block
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool DUMMY = PropertyBool.create("dummy");

	private final IGMultiblockStructure structure;
	private final Supplier<TileEntity> tileFactory;

	public IGMultiblockBlock(IGMultiblockStructure structure, Supplier<TileEntity> tileFactory)
	{
		super(Material.IRON);
		this.structure = structure;
		this.tileFactory = tileFactory;

		setHardness(3.0F);
		setResistance(15.0F);
		setDefaultState(blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(DUMMY, false));
	}

	public IGMultiblockStructure getStructure()
	{
		return structure;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{FACING, DUMMY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState()
				.withProperty(FACING, EnumFacing.byHorizontalIndex(meta&3))
				.withProperty(DUMMY, (meta&4)!=0);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex()|(state.getValue(DUMMY)?4: 0);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return tileFactory.get();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityMultiblockPart<?> part) part.disassemble();
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
									EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityMultiblockPart<?> part&&part.formed)
			return true;
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}
}
