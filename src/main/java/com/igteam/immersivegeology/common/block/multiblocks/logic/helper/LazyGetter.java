/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import java.util.List;
import java.util.function.Function;

public class LazyGetter<B,T> {
	private final Function<B, T> supplier;
	private T list = null;

	public LazyGetter(Function<B, T> supplier) {
		this.supplier = supplier;
	}

	public T get(B blockPos) {
		if (list == null) {
			list = supplier.apply(blockPos);
		}
		return list;
	}

	public void reset() {
		list = null;
	}
}
