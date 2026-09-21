package com.igteam.immersivegeology.core.material.helper.material.recipe;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;

public class IGRecipeMethod extends IGRecipeNode
{
	protected final MaterialHelper parentMaterial;
	protected final IGStageDesignation stage;

	public IGRecipeMethod(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		this.parentMaterial = parentMaterial;
		this.stage = stage;
	}

	public MaterialHelper getParentMaterial()
	{
		return parentMaterial;
	}

	public IGStageDesignation getStage()
	{
		return stage;
	}

	public IGRecipeMethod create(String name, Object... arguments)
	{
		return this;
	}

	public IGRecipeMethod create(Object... arguments)
	{
		return this;
	}

	public IGRecipeMethod setBurntime(Object... context)
	{
		return this;
	}
}
