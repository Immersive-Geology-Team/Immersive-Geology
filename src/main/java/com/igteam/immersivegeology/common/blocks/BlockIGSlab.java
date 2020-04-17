package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IColouredBlock;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class BlockIGSlab<T extends Block & IIGBlock> extends SlabBlock implements IIGBlock, IColouredBlock
{
	private final T base;
	public IGBlockItem itemBlock = null;
	private boolean hasCustomColor;

	public BlockIGSlab(String name, Properties props, Class<? extends BlockItem> itemBlock, T base, ItemSubGroup group)
	{
		super(props);
		ResourceLocation registryName = new ResourceLocation(ImmersiveGeology.MODID, name);
		setRegistryName(registryName);
		IGContent.registeredIGBlocks.add(this);
		this.base = base;
		this.hasCustomColor = base instanceof IColouredBlock&&((IColouredBlock)base).hasCustomBlockColours();

		if(itemBlock!=null)
		{
			try
			{
				this.itemBlock = (IGBlockItem)itemBlock.getConstructor(Block.class, Item.Properties.class, ItemSubGroup.class)
						.newInstance(this, new Item.Properties().group(ImmersiveGeology.IG_ITEM_GROUP), group);
				this.itemBlock.setRegistryName(registryName);
				IGContent.registeredIGItems.add(this.itemBlock);
			} catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}


	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
	{
		double relativeEntityPosition = entity.getPosition().getY()-pos.getY();
		switch(state.get(SlabBlock.TYPE))
		{
			case TOP:
				return 0.5D < relativeEntityPosition&&relativeEntityPosition < 1.0D;
			case BOTTOM:
				return 0.0D < relativeEntityPosition&&relativeEntityPosition < 0.5D;
			case DOUBLE:
				return true;
			default:
				return false;
		}
	}

	public boolean hasFlavour()
	{
		return (this.base).hasFlavour();
	}

	@Override
	public Item getItemBlock()
	{
		return this.itemBlock;
	}

	public String getNameForFlavour()
	{
		return (this.base).getNameForFlavour();
	}

	public BlockRenderLayer getRenderLayer()
	{
		return this.base.getRenderLayer();
	}

	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
	{
		return this.base.canRenderInLayer(this.base.getDefaultState(), layer);
	}

	public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return this.base.getOpacity(state, worldIn, pos);
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return this.base.propagatesSkylightDown(state, reader, pos);
	}

	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return this.base.causesSuffocation(state, worldIn, pos);
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return this.base.isNormalCube(state, worldIn, pos);
	}

	@Override
	public boolean hasCustomBlockColours()
	{
		return hasCustomColor;
	}

	@Override
	public int getRenderColour(BlockState blockState, @Nullable IBlockReader iBlockReader, @Nullable BlockPos blockPos, int i)
	{
		return ((IColouredBlock)base).getRenderColour(blockState, iBlockReader, blockPos, i);
	}
}
