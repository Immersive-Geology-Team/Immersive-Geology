/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.common.network.PacketUtils;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.TurbineFuelBuilder.*;

public class TurbineFuelSerializer extends IERecipeSerializer<TurbineFuel>
{
	public static final MapCodec<TurbineFuel> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Codec.mapEither(
					TagKey.codec(Registries.FLUID).fieldOf(FLUID_TAG_KEY),
					BuiltInRegistries.FLUID.byNameCodec().listOf().fieldOf("fluidList")
			).forGetter(f -> f.getFluidsRaw().map(Either::left, Either::right)),
			Codec.INT.fieldOf(CONSUME_AMOUNT_KEY).forGetter(TurbineFuel::getConsumed),
			Codec.INT.fieldOf(BURN_TIME_KEY).forGetter(TurbineFuel::getBurnTime),
			Codec.FLOAT.fieldOf(OUTPUT_RATIO).forGetter(TurbineFuel::getOutputRatio)
	).apply(inst, (fluids, consumeAmount, burnTime, outputRatio) -> fluids.map(
			tag -> new TurbineFuel(tag, outputRatio, consumeAmount, burnTime),
			list -> new TurbineFuel(list, outputRatio, consumeAmount, burnTime)
	)));

	public static final StreamCodec<RegistryFriendlyByteBuf, TurbineFuel> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.registry(Registries.FLUID).apply(ByteBufCodecs.list()), TurbineFuel::getFluids,
			ByteBufCodecs.INT, TurbineFuel::getConsumed,
			ByteBufCodecs.INT, TurbineFuel::getBurnTime,
			ByteBufCodecs.FLOAT, TurbineFuel::getOutputRatio,
			(fluids, consumeAmount, burnTime, outputRatio) -> new TurbineFuel(fluids, outputRatio, consumeAmount, burnTime)
	);

	public static final DualMapCodec<RegistryFriendlyByteBuf, TurbineFuel> CODECS = new DualMapCodec<>(CODEC, STREAM_CODEC);

	@Override
	protected DualMapCodec<RegistryFriendlyByteBuf, TurbineFuel> codecs()
	{
		return CODECS;
	}

	@Override
	public ItemStack getIcon()
	{
		return IGMultiblockProvider.STEAM_TURBINE.iconStack();
	}
}