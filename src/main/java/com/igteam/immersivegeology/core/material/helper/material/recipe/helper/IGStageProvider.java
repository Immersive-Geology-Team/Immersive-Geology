/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;

import java.util.HashMap;
import java.util.Set;

public class IGStageProvider
{
	private static HashMap<MaterialHelper, Set<IGRecipeStage>> stageMap = new HashMap<>();

	public static void add(MaterialHelper material, Set<IGRecipeStage> stage){
		stageMap.put(material, stage);
	}

	public static Set<IGRecipeStage> get(MaterialHelper material) {
		return stageMap.get(material);
	}
}
