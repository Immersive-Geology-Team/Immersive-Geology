package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.GeologyMaterial;

import java.util.ArrayList;
import java.util.List;

public class IGRecipeChain
{
	private final GeologyMaterial material;
	private final String name;
	private final int index;

	private final List<IGRecipeNode> rootNodes = new ArrayList<>();

	public IGRecipeChain(GeologyMaterial material, String name, int index)
	{
		this.material = material;
		this.name = name;
		this.index = index;
	}

	public GeologyMaterial getMaterial()
	{
		return material;
	}

	public String getName()
	{
		return name;
	}

	public int getIndex()
	{
		return index;
	}

	public List<IGRecipeNode> getRootNodes()
	{
		return rootNodes;
	}
}
