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
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class IGDefaultPlacement extends PlacementModifier
{
	private static final IGDefaultPlacement INSTANCE = new IGDefaultPlacement();
	public static final Codec<IGDefaultPlacement> PLACEMENT_CODEC = Codec.unit(() -> {
		return INSTANCE;
	});

	public IGDefaultPlacement() {
	}

	public static IGDefaultPlacement spread() {
		return INSTANCE;
	}

	public @NotNull Stream<BlockPos> getPositions(@NotNull PlacementContext ctx, RandomSource rnd, @NotNull BlockPos pos) {
		int $$3 = rnd.nextInt(16) + pos.getX();
		int $$4 = rnd.nextInt(16) + pos.getZ();
		return rnd.nextInt(3) == 0 ? Stream.of(new BlockPos($$3, pos.getY(), $$4)) : Stream.of();
	}

	public PlacementModifierType<?> type() {
		return IGWorldGen.IG_DEFAULT_PLACEMENT.get();
	}
}
