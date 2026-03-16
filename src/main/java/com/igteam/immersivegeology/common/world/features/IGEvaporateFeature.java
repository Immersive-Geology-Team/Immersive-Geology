/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features;

import com.igteam.immersivegeology.common.block.ore.IGWeatheringOreBlock;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.IGOreFeature.IGOreFeatureConfig;
import com.igteam.immersivegeology.common.world.features.helper.IGEvaporateConfig;
import com.igteam.immersivegeology.common.world.features.helper.IGOreGenUtils;
import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class IGEvaporateFeature extends Feature<IGEvaporateConfig>
{
	public IGEvaporateFeature(){
		super(IGEvaporateConfig.CODEC.codec());
	}

	@Override
	public boolean place(FeaturePlaceContext<IGEvaporateConfig> context) {
		WorldGenLevel world = context.level();
		BlockPos origin = context.origin();
		RandomSource random = context.random();
		BlockState evaporiteBlock = context.config().entry().getDefaultBlockstate();

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