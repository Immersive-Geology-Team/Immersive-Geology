/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui.sync;

import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers.DataSerializer;
import com.igteam.immersivegeology.common.block.multiblocks.gui.helper.IGSlot;
import com.igteam.immersivegeology.mixin.accessors.GenericDataSerializerAccessor;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class IGGenericDataSerializers
{
	public static final DataSerializer<List<IGSlot.RotarySlot>> ROTARYKILN_PROCESSES = register(
			(fbb) -> fbb.readList(IGSlot.RotarySlot::from),
			(fbb, processes) -> fbb.writeCollection(processes, IGSlot.RotarySlot::writeTo)
	);

	private static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write) {
		return register(read, write, (t) -> {
			return t;
		}, Objects::equals);
	}

	private static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write, UnaryOperator<T> copy, BiPredicate<T, T> equals) {
		DataSerializer<T> serializer = new DataSerializer<>(read, write, copy, equals, GenericDataSerializerAccessor.getSerializers().size());
		GenericDataSerializerAccessor.getSerializers().add(serializer);
		return serializer;
	}
}
