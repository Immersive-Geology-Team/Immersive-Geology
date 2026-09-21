package com.igteam.immersivegeology.core.material.helper.material.recipe;

public class IGRecipeStage
{
	private final IGStageDesignation designation;

	public IGRecipeStage(IGStageDesignation designation)
	{
		this.designation = designation;
	}

	public IGStageDesignation getDesignation()
	{
		return designation;
	}
}
