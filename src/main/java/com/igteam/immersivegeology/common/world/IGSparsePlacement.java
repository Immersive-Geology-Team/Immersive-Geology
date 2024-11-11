/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class IGSparsePlacement extends PlacementModifier
{
	private static final IGSparsePlacement INSTANCE = new IGSparsePlacement();
	public static final Codec<IGSparsePlacement> CODEC = Codec.unit(() -> {
		return INSTANCE;
	});

	public IGSparsePlacement() {
	}

	public static IGSparsePlacement spread() {
		return INSTANCE;
	}

	public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource rnd, BlockPos pos) {
		int $$3 = rnd.nextInt(16) + pos.getX();
		int $$4 = rnd.nextInt(16) + pos.getZ();
		return rnd.nextInt(16) == 0 ? Stream.of(new BlockPos($$3, pos.getY(), $$4)) : Stream.of();
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_SPARSE_PLACEMENT.get();
	}
}
