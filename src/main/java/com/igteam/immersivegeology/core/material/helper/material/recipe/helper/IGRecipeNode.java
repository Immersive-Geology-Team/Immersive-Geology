/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IGRecipeNode
{
	private IGRecipeMethod method;
	private final List<IGRecipeNode> children = new ArrayList<>();
	private final List<IGRecipeNode> parents = new ArrayList<>();
	public boolean shouldRender = true;

	// Layout coordinates (for rendering)
	private int x;
	private int y;

	public IGRecipeNode(IGRecipeMethod method) {
		this.method = method;
	}

	public IGRecipeMethod getMethod() {
		return method;
	}

	public List<IGRecipeNode> getChildren() {
		return children;
	}

	public List<IGRecipeNode> getParents() {
		return parents;
	}

	public void addChild(IGRecipeNode child) {
		children.add(child);
		child.addParent(this);
	}

	public void addParent(IGRecipeNode parent) {
		parents.add(parent);
	}

	public void resetRender()
	{
		shouldRender = true;
		for(IGRecipeNode c : children)
		{
			if(!c.shouldRender) c.resetRender();
		}
	}

	// Getters and setters for layout coordinates.
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
}
