package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;

public class IGCrystallizationMethod extends IGRecipeMethod
{
	public IGCrystallizationMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(parentMaterial, stage);
	}

	@Override
	public IGCrystallizationMethod create(Object... arguments)
	{
		return this;
	}

	@Override
	public IGCrystallizationMethod create(String name, Object... arguments)
	{
		return this;
	}


}
