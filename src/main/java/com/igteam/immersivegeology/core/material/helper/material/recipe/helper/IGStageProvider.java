package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;

import java.util.Collections;
import java.util.Set;

public final class IGStageProvider
{
	private IGStageProvider()
	{
	}

	public static Set<IGRecipeStage> get(MaterialHelper material)
	{
		return Collections.emptySet();
	}

	public static void add(MaterialHelper material, Set<IGRecipeStage> stages)
	{
	}
}
