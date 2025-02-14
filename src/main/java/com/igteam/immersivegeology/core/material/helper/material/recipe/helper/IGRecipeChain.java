/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;

import java.util.*;

public class IGRecipeChain
{
	private final GeologyMaterial material;
	private final String chainName;
	private final int priority;

	// The root node of the chain.
	private final List<IGRecipeNode> rootNodes = new ArrayList<>();


	public IGRecipeChain(GeologyMaterial material, String chainName, int priority) {
		this.material = material;
		this.chainName = chainName;
		this.priority = priority;
	}

	/**
	 * Adds a method as the next step in a linear chain.
	 */
	public void addMethod(IGRecipeMethod method) {
		IGRecipeNode newNode = new IGRecipeNode(method);
		method.setNode(newNode);
		if (rootNodes.isEmpty()) {
			rootNodes.add(newNode);
		} else {
			// For linear chains, assume the first root is used.
			IGRecipeNode last = getLastNode(rootNodes.get(0));
			last.addChild(newNode);
		}
	}

	public IGRecipeNode addOptionalRoot(IGRecipeMethod method) {
		IGRecipeNode newNode = new IGRecipeNode(method);
		method.setNode(newNode);
		rootNodes.add(newNode);
		return newNode;
	}

	/**
	 * Adds a method as a branch (child) from an existing parent method.
	 */
	public void addChild(IGRecipeMethod parentMethod, IGRecipeMethod childMethod) {
		IGRecipeNode parentNode = parentMethod.getNode();
		if (parentNode == null) {
			parentNode = new IGRecipeNode(parentMethod);
			parentMethod.setNode(parentNode);
			if (rootNodes.isEmpty()) {
				rootNodes.add(parentNode);
			}
		}
		IGRecipeNode childNode = new IGRecipeNode(childMethod);
		childMethod.setNode(childNode);
		parentNode.addChild(childNode);
	}

	public void promoteOptionalRoot(IGRecipeNode optionalRoot, IGRecipeMethod newParentMethod) {
		// Remove the optional root from the list.
		rootNodes.remove(optionalRoot);
		// Add it as a child of newParentMethod.
		addChild(newParentMethod, optionalRoot.getMethod());
	}

	public void join(IGRecipeNode branch1, IGRecipeNode branch2, IGRecipeMethod joinMethod) {
		IGRecipeNode joinNode = new IGRecipeNode(joinMethod);
		joinMethod.setNode(joinNode);

		// Add joinNode as a child of branch1 and branch2.
		branch1.addChild(joinNode);
		branch2.addChild(joinNode);
	}

	// Helper method for a linear chain.
	private IGRecipeNode getLastNode(IGRecipeNode node) {
		if (node.getChildren().isEmpty()) {
			return node;
		} else {
			return getLastNode(node.getChildren().get(0));
		}
	}

	public List<IGRecipeNode> getRootNodes() {
		return rootNodes;
	}

	public void layoutRecipeChain(IGRecipeNode root, int startX, int startY, int verticalSpacing, int horizontalSpacing) {
		Map<IGRecipeNode, Integer> xOffsets = new HashMap<>();
		assignNodePositions(root, startX, startY, verticalSpacing, horizontalSpacing, xOffsets);
	}

	/**
	 * Recursively assigns X and Y positions to nodes.
	 */
	private int assignNodePositions(IGRecipeNode node, int x, int y, int vSpacing, int hSpacing, Map<IGRecipeNode, Integer> xOffsets) {
		node.setX(x);
		node.setY(y);

		List<IGRecipeNode> children = node.getChildren();
		int numChildren = children.size();

		if (numChildren == 0) {
			// No children, just return the X position
			return x;
		}

		// Calculate the starting X position for children to spread evenly
		int minX = x;
		int maxX = x;

		int childX = x - (numChildren - 1) * hSpacing / 2; // Centering children around the parent

		for (IGRecipeNode child : children) {
			int newX = assignNodePositions(child, childX, y + vSpacing, vSpacing, hSpacing, xOffsets);
			minX = Math.min(minX, newX);
			maxX = Math.max(maxX, newX);
			childX += hSpacing;
		}

		// Align parent node with the middle of its children
		int centerX = (minX + maxX) / 2;
		node.setX(centerX);

		return centerX;
	}

	public int getPriority()
	{
		return priority;
	}

	public String getName()
	{
		return chainName.toLowerCase();
	}
}