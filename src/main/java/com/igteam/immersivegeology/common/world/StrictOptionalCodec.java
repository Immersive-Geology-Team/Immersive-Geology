/*
 * Copyright (c) 2021 AlcatrazEscapee
 * This code is licensed under the MIT License.
 * This code is from the project Cyanide.
 */

package com.igteam.immersivegeology.common.world;

import java.util.Optional;
import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
public final class StrictOptionalCodec<A> extends MapCodec<Optional<A>>
{
	private final String name;
	private final Codec<A> elementCodec;

	public StrictOptionalCodec(String name, Codec<A> elementCodec)
	{
		this.name = name;
		this.elementCodec = elementCodec;
	}

	@Override
	public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input)
	{
		final T value = input.get(name);
		if (value != null)
		{
			return elementCodec.parse(ops, value)
					.map(Optional::of)
					.mapError(e -> "Optional field \"" + name + "\" was invalid: " + e);
		}
		return DataResult.success(Optional.empty());
	}

	@Override
	public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix)
	{
		if (input.isPresent())
		{
			return prefix.add(name, elementCodec.encodeStart(ops, input.get()));
		}
		return prefix;
	}

	@Override
	public <T> Stream<T> keys(final DynamicOps<T> ops)
	{
		return Stream.of(ops.createString(name));
	}
}
