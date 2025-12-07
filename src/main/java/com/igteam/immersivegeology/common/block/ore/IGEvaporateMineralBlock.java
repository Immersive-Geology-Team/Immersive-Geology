/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.ore;

import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class IGEvaporateMineralBlock extends IGGenericBlock
{

	private final Supplier<IGCrystalBlock> clusters;

	public IGEvaporateMineralBlock(BlockCategoryFlags flag, MaterialInterface<?> material, Supplier<IGCrystalBlock> clusterType)
	{
		super(flag, material, BlockBehaviour.Properties.copy(Blocks.SAND).randomTicks().mapColor(MapColor.SAND));
		this.clusters = clusterType;
	}

	@Override
	public @Nullable PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.DESTROY;
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState)
	{
		return super.isRandomlyTicking(pState);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved)
	{
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);

		// Check if the block is adjacent to water (a water source or flowing water)
		for (Direction direction : Direction.values()) {
			BlockPos adjacentPos = pos.relative(direction);
			BlockState adjacentState = level.getBlockState(adjacentPos);

			if (adjacentState.getFluidState().is(FluidTags.WATER)) {
				level.destroyBlock(pos, false); // Break the block
				break; // Once water is found, break the loop
			}
		}
	}
	int tick = 0;
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
	{
		tick++;
		if(tick % 10 == 0)
		{
			tick = 0;
			if(random.nextBoolean())
			{
				Direction direction = Direction.UP;
				BlockPos blockPos = pos.offset(direction.getNormal());
				BlockState blockState = world.getBlockState(blockPos);
				IGCrystalBlock nextBlock = null;

				if(blockState.isAir())
				{
					nextBlock = clusters.get();
				}

				if(nextBlock!=null)
				{
					BlockState toSet = nextBlock.defaultBlockState();

					world.setBlockAndUpdate(blockPos, toSet);
				}
			}
		}
	}

	public ItemStack getItemDrop()
	{
		return new ItemStack(getMaterial(MaterialTexture.base).getItem(ItemCategoryFlags.SEDIMENT));
	}
}
