/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.GeothermalConversionRecipe;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.datafixers.util.Pair;
import malte0811.dualcodecs.DualCodec;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class GeothermalConversionRecipeSerializer extends IERecipeSerializer<GeothermalConversionRecipe>
{
	private static final DualCodec<RegistryFriendlyByteBuf, Block> BLOCK_CODECS = new DualCodec<>(
			ResourceLocation.CODEC.xmap(BuiltInRegistries.BLOCK::get, BuiltInRegistries.BLOCK::getKey),
			ByteBufCodecs.registry(Registries.BLOCK)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, GeothermalConversionRecipe> CODECS = DualCompositeMapCodecs.composite(
			BLOCK_CODECS.fieldOf("transitionBlock"), r -> r.transitionBlock.get(),
			DualCodecs.INT.fieldOf("blockHeat"), r -> r.blockHeat,
			BLOCK_CODECS.optionalFieldOf("upperBoundBlock"), r -> Optional.ofNullable(r.upperTransition),
			DualCodecs.INT.optionalFieldOf("upperHeat"), r -> Optional.ofNullable(r.upperHeat),
			BLOCK_CODECS.optionalFieldOf("lowerBoundBlock"), r -> Optional.ofNullable(r.lowerTransition),
			DualCodecs.INT.optionalFieldOf("lowerHeat"), r -> Optional.ofNullable(r.lowerHeat),
			(transitionBlock, blockHeat, upperBlock, upperHeat, lowerBlock, lowerHeat) -> {
				Pair<Block, Integer> upperBound = upperBlock.map(block -> Pair.of(block, upperHeat.orElseThrow(
						() -> new IllegalArgumentException("Missing required field 'upperHeat' for 'upperBoundBlock'.")
				))).orElse(null);
				Pair<Block, Integer> lowerBound = lowerBlock.map(block -> Pair.of(block, lowerHeat.orElseThrow(
						() -> new IllegalArgumentException("Missing required field 'lowerHeat' for 'lowerBoundBlock'.")
				))).orElse(null);
				return new GeothermalConversionRecipe(() -> transitionBlock, blockHeat, upperBound, lowerBound);
			}
	);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, GeothermalConversionRecipe> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.GEOTHERMAL_EXCHANGER.iconStack();
	}
}
