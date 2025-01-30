/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.common.Tags;

public class IGEvaporateFeature extends Feature<BlockStateConfiguration>
{
	public IGEvaporateFeature(Codec<BlockStateConfiguration> codec) {
		super(codec);
	}
	@Override
	public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
		WorldGenLevel world = context.level();
		BlockPos origin = context.origin();
		RandomSource random = context.random();
		BlockState evaporiteBlock = context.config().state;

		boolean isWet = false;
		int radius = 8; // Random radius (2-4 blocks)
		int height = radius / 2 + 1; // Semi-dome height

		for (int x = -radius; x <= radius; x++)
		{
			for(int z = -radius; z <= radius; z++)
			{
				int distance = x*x+z*z;
				if(distance <= radius*radius)
				{
					for(int y = 0; y <= height; y++)
					{
						BlockPos pos = origin.offset(x, -y, z);
						if(world.isFluidAtPosition(pos, (s) -> s.is(Fluids.WATER)))
						{
							isWet = true;
							break;
						}
					}
				}
			}
		}

		radius = 2 + random.nextInt(3); // Random radius (2-4 blocks)
		height = radius / 2 + 1; // Semi-dome height

		// Ensure we place only on sand
		if (!world.getBlockState(origin.below()).is(Tags.Blocks.SAND) || isWet) {
			return false;
		}

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				int distance = x * x + z * z;
				if (distance <= radius * radius) {
					for (int y = 0; y <= height; y++) {
						BlockPos pos = origin.offset(x, -y, z);
						if (world.getBlockState(pos).is(Tags.Blocks.SAND)) {
							world.setBlock(pos, evaporiteBlock, 2);
						}
					}
				}
			}
		}
		return true;
	}
}
