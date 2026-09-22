package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class IGSlabBlock extends BlockSlab
{
	public enum Variant implements IStringSerializable
	{
		DEFAULT;

		@Override
		public String getName()
		{
			return "default";
		}
	}

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	private final boolean doubleSlab;
	private final BlockCategoryFlags category;
	private final MaterialInterface<?> material;

	private IGSlabBlock singleSlab;

	public IGSlabBlock(BlockCategoryFlags category, MaterialInterface<?> material, boolean doubleSlab)
	{
		super(materialFor(category));
		this.category = category;
		this.material = material;
		this.doubleSlab = doubleSlab;

		IBlockState state = blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
		if(!doubleSlab) state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
		setDefaultState(state);

		setHardness(2.0F);
		setResistance(10.0F);
		useNeighborBrightness = !doubleSlab;
	}

	private static Material materialFor(BlockCategoryFlags category)
	{
		return category==BlockCategoryFlags.SHEETMETAL_SLAB?Material.IRON: Material.ROCK;
	}

	public void setSingleSlab(IGSlabBlock singleSlab)
	{
		this.singleSlab = singleSlab;
	}

	public BlockCategoryFlags getCategory()
	{
		return category;
	}

	public MaterialInterface<?> getMaterial()
	{
		return material;
	}

	@Override
	public boolean isDouble()
	{
		return doubleSlab;
	}

	@Override
	public IProperty<?> getVariantProperty()
	{
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack)
	{
		return Variant.DEFAULT;
	}

	@Override
	public String getTranslationKey(int meta)
	{
		return getTranslationKey();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return doubleSlab
				?new BlockStateContainer(this, VARIANT)
				: new BlockStateContainer(this, HALF, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState state = getDefaultState();
		if(doubleSlab) return state;
		return state.withProperty(HALF, (meta&8)!=0?EnumBlockHalf.TOP: EnumBlockHalf.BOTTOM);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(doubleSlab) return 0;
		return state.getValue(HALF)==EnumBlockHalf.TOP?8: 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(singleSlab==null?this: singleSlab);
	}

	@Override
	public ItemStack getItem(net.minecraft.world.World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(singleSlab==null?this: singleSlab);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return doubleSlab;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return doubleSlab;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos,
										  net.minecraft.util.EnumFacing face)
	{
		return doubleSlab||super.doesSideBlockRendering(state, world, pos, face);
	}
}
