/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Objects;
import java.util.Optional;

public class CodecHelper
{
	public static <T> MapCodec<Optional<T>> optionalFieldOf(Codec<T> codec, String field) {
		return new StrictOptionalCodec<>(field, codec);
	}

	public static <T> MapCodec<T> optionalFieldOf(Codec<T> codec, String field, T defaultValue) {
		return optionalFieldOf(codec, field).xmap((o) -> {
			return o.orElse(defaultValue);
		}, (a) -> {
			return Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a);
		});
	}
}
