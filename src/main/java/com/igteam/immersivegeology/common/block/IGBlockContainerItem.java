/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class IGBlockContainerItem extends IGGenericBlockItem
{
	public IGBlockContainerItem(IGBlockType block)
	{
		super(block);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState newState)
	{
		Block b = newState.getBlock();
		if(b instanceof IEBaseBlock ieBlock)
		{
			if(!ieBlock.canIEBlockBePlaced(newState, context))
				return false;
			boolean ret = super.placeBlock(context, newState);
			if(ret)
				ieBlock.onIEBlockPlacedBy(context, newState);
			return ret;
		}
		else
			return super.placeBlock(context, newState);
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state)
	{
		// Skip reading the tile from NBT if the block is a (general) multiblock
		if(!state.hasProperty(IEProperties.MULTIBLOCKSLAVE))
			return super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
		else
			return false;
	}

	@Nonnull
	@Override
	public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack)
	{
		if(stack.hasTag())
		{
			CompoundTag tag = stack.getOrCreateTag();
			if(tag.contains("Items"))
			{
				// manual readout, skipping empty slots
				ListTag list = tag.getList("Items", 10);
				NonNullList<ItemStack> items = NonNullList.create();
				list.forEach(e -> {
					ItemStack s = ItemStack.of((CompoundTag)e);
					if(!s.isEmpty())
						items.add(s);
				});
				return Optional.of(new BundleTooltip(items, 0));
			}
		}
		return super.getTooltipImage(stack);
	}

	@Override
	public boolean canFitInsideContainerItems()
	{
		return !(getBlock() instanceof IEBaseBlock ieBlock)||ieBlock.fitsIntoContainer();
	}
}
