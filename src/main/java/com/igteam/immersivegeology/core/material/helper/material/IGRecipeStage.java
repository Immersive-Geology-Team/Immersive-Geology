/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class IGRecipeStage
{
	private final String stage_name;
	private String stage_description;

	private LinkedHashSet<IGRecipeMethod> recipeMethodSet = new LinkedHashSet<>();

	protected IGRecipeStage(String stageName)
	{
		stage_name = stageName;
	}
}
