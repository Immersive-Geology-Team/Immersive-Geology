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
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntity;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntityType;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
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
		super(block, new Properties().fireResistant());
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState newState)
	{
		Block b = newState.getBlock();
		if(b instanceof IEBaseBlock ieBlock)
		{
			if(!ieBlock.canIEBlockBePlaced(newState, context))
				return false;
			ItemStack itemStack = context.getItemInHand();
			if(b instanceof IGCrateEntityType && !itemStack.has(DataComponents.CONTAINER))
			{
				Tag itemsTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().get("Items");
				if(itemsTag != null)
				{
					if(!(itemsTag instanceof ListTag legacyItems))
					{
						IGLib.IG_LOGGER.warn("Invalid crate inventory at {}", context.getClickedPos());
						return false;
					}
					ListTag slots = new ListTag();
					for(Tag tag : legacyItems)
					{
						if(!(tag instanceof CompoundTag item) || item.contains("Count") || (item.getByte("Slot") & 255) >= IGCrateEntity.CONTAINER_SIZE)
						{
							IGLib.IG_LOGGER.warn("Invalid crate inventory at {}", context.getClickedPos());
							return false;
						}
						CompoundTag entry = new CompoundTag();
						entry.putInt("slot", item.getByte("Slot") & 255);
						entry.put("item", item);
						slots.add(entry);
					}
					ItemContainerContents contents = ItemContainerContents.CODEC
							.parse(context.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), slots)
							.ifError(error -> IGLib.IG_LOGGER.warn("Tried to load invalid crate inventory at {}: {}", context.getClickedPos(), error.message()))
							.result().orElse(null);
					if(contents==null) return false;
					if(!contents.equals(ItemContainerContents.EMPTY))
						itemStack.set(DataComponents.CONTAINER, contents);
					CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.remove("Items"));
				}
			}
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
		ItemContainerContents items = stack.get(DataComponents.CONTAINER);
		if(items != null)
			return Optional.of(new BundleTooltip(new BundleContents(items.nonEmptyStream().toList())));
		else
		{
			CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
			Level level = ImmersiveGeology.proxy.getClientWorld();
			if(level != null && data.contains("Items"))
			{
				NonNullList<ItemStack> legacyItems = NonNullList.withSize(IGCrateEntity.CONTAINER_SIZE, ItemStack.EMPTY);
				ContainerHelper.loadAllItems(data.copyTag(), legacyItems, level.registryAccess());
				items = ItemContainerContents.fromItems(legacyItems);
				return Optional.of(new BundleTooltip(new BundleContents(items.nonEmptyStream().toList())));
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
