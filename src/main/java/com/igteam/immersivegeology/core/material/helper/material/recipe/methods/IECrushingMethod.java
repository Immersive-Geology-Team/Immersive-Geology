package com.igteam.immersivegeology.core.material.helper.material.recipe.methods;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;

public class IECrushingMethod extends IGRecipeMethod
{
	public IECrushingMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		super(parentMaterial, stage);
	}

	@Override
	public IECrushingMethod create(Object... arguments)
	{
		return this;
	}

	@Override
	public IECrushingMethod create(String name, Object... arguments)
	{
		return this;
	}

}
