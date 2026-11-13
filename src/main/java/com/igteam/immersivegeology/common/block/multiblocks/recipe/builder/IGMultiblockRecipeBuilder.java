/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

public abstract class IGMultiblockRecipeBuilder<B extends IGMultiblockRecipeBuilder<B>> {
	protected final List<ICondition> conditions = new ArrayList<>();

	public B addCondition(ICondition condition) {
		conditions.add(condition);
		return (B) this;
	}

	protected ICondition[] getConditions() {
		return conditions.toArray(ICondition[]::new);
	}
}
