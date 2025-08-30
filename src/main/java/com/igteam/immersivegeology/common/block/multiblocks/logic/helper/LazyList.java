/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.logic.helper;

import com.igteam.immersivegeology.core.lib.IGLib;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyList<B,T> {
	private final Function<B, List<T>> supplier;
	private List<T> list = null;
	private B blockPos;

	public LazyList(Function<B, List<T>> supplier) {
		this.supplier = supplier;
	}

	public List<T> get(B blockPos) {
		if (list == null) {
			this.blockPos = blockPos;
			IGLib.IG_LOGGER.info("Initializing list with BlockPos: {}", blockPos);
			list = supplier.apply(blockPos);
		}
		return list;
	}

	public void reset() {
		list = null;
	}

}
