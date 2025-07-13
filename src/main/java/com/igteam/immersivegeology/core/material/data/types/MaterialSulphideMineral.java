/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.mojang.datafixers.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaterialSulphideMineral extends MaterialMineral
{

	public MaterialSulphideMineral()
	{
		super();
		acceptableStoneTypes.add(StoneFormation.NETHER_STONE);
	}

	@Override
	public Set<IGRecipeChain> getRecipeChains()
	{
		if(hasFlag(ItemCategoryFlags.PELLET) &! this.directBlasting.getRootNodes().isEmpty()) return Set.of(this.directBlasting);
		return Set.of();
	}

	public List<String> getAcceptableDimensions()
	{
		return List.of("minecraft:the_nether");
	}
}
