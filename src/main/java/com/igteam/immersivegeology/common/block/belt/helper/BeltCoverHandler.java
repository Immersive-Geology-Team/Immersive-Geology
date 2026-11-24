/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDecoration;
import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.IGSplitterBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = IGLib.MODID, bus = EventBusSubscriber.Bus.FORGE)
public final class BeltCoverHandler
{
	private static final String COVERED_SUFFIX = "_covered";

	private BeltCoverHandler()
	{
	}

	public static boolean isCover(ItemStack stack)
	{
		return stack.getItem() instanceof BlockItem item&&item.getBlock().defaultBlockState().is(IETags.scaffoldingSteel);
	}

	public static Optional<IGBeltBlock> counterpart(Block block)
	{
		ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
		if(id==null)
			return Optional.empty();
		String path = id.getPath();
		String other = path.endsWith(COVERED_SUFFIX)?path.substring(0, path.length()-COVERED_SUFFIX.length()): path+COVERED_SUFFIX;
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id.getNamespace(), other)) instanceof IGBeltBlock belt?Optional.of(belt): Optional.empty();
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Player player = event.getEntity();
		ItemStack held = event.getItemStack();
		if(!player.isSecondaryUseActive()||!isCover(held))
			return;
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		if(!(state.getBlock() instanceof IGBeltBlock belt)||belt.style().covered())
			return;
		if(counterpart(belt).isEmpty())
			return;
		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
		if(level.isClientSide)
			return;
		swap(level, pos, state);
		if(!player.getAbilities().instabuild)
			held.shrink(1);
		level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1, 1);
	}

	public static int offhandCovers(Player player, Block block)
	{
		if(!(block instanceof IGBeltBlock belt)||belt.style().covered()||counterpart(belt).isEmpty())
			return 0;
		ItemStack offhand = player.getOffhandItem();
		if(!isCover(offhand))
			return 0;
		return player.getAbilities().instabuild?Integer.MAX_VALUE: offhand.getCount();
	}

	public static void useOffhandCover(Player player)
	{
		if(!player.getAbilities().instabuild)
			player.getOffhandItem().shrink(1);
	}

	public static void coverPlaced(Level level, BlockPos pos, Player player, Block placed)
	{
		if(level.isClientSide)
			return;
		BlockState state = level.getBlockState(pos);
		if(!state.is(placed)||offhandCovers(player, placed) < 1)
			return;
		swap(level, pos, state);
		useOffhandCover(player);
	}

	public static InteractionResult uncover(Level level, BlockPos pos, BlockState state, Player player)
	{
		if(!(state.getBlock() instanceof IGBeltBlock belt)||!belt.style().covered()||counterpart(belt).isEmpty())
			return InteractionResult.PASS;
		if(level.isClientSide)
			return InteractionResult.SUCCESS;
		swap(level, pos, state);
		if(!player.getAbilities().instabuild)
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD)));
		level.playSound(null, pos, SoundEvents.METAL_BREAK, SoundSource.BLOCKS, 1, 1);
		return InteractionResult.CONSUME;
	}

	private static List<BlockPos> cells(BlockState state, BlockPos pos)
	{
		List<BlockPos> cells = new ArrayList<>();
		cells.add(pos);
		if(state.getBlock() instanceof IGSplitterBlock)
			cells.add(pos.relative(IGSplitterBlock.partnerDirection(state)));
		return cells;
	}

	static void swap(Level level, BlockPos pos, BlockState state)
	{
		IGBeltBlock target = counterpart(state.getBlock()).orElseThrow();
		List<BlockPos> cells = cells(state, pos);
		for(BlockPos cell : cells)
		{
			BlockState old = level.getBlockState(cell);
			CompoundTag saved = null;
			BlockEntity entity = level.getBlockEntity(cell);
			if(entity!=null)
			{
				saved = entity.saveWithoutMetadata();
				if(entity instanceof IGBeltBlockEntity belt)
					belt.takeContents();
			}
			BlockState next = copyProperties(old, target.defaultBlockState());
			level.setBlock(cell, next, Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
			if(saved!=null&&level.getBlockEntity(cell) instanceof IGBeltBlockEntity belt)
			{
				belt.load(saved);
				belt.markLanesChanged();
				belt.flushSync();
			}
		}
		for(BlockPos cell : cells)
		{
			BlockState current = level.getBlockState(cell);
			level.blockUpdated(cell, current.getBlock());
			current.updateNeighbourShapes(level, cell, Block.UPDATE_ALL);
		}
		IGBeltBlock.refreshAround(level, pos);
	}

	private static BlockState copyProperties(BlockState from, BlockState to)
	{
		for(Property<?> property : from.getProperties())
		{
			Property<?> match = to.getBlock().getStateDefinition().getProperty(property.getName());
			if(match!=null)
				to = copy(to, match, from.getValue(property));
		}
		return to;
	}

	private static <T extends Comparable<T>> BlockState copy(BlockState state, Property<T> property, Comparable<?> value)
	{
		return property.getValueClass().isInstance(value)&&property.getPossibleValues().contains(value)?state.setValue(property, property.getValueClass().cast(value)): state;
	}
}
