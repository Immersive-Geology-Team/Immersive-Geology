package com.igteam.immersivegeology.core.material.helper.material.recipe;

public class IGRecipeStage
{
	private final IGStageDesignation designation;

	private final String name;

	public IGRecipeStage(IGStageDesignation designation)
	{
		this.designation = designation;
		this.name = designation==null?"": designation.name();
	}

	public IGRecipeStage(String name)
	{
		this.designation = null;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public IGStageDesignation getDesignation()
	{
		return designation;
	}
}
