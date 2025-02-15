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
import com.mojang.datafixers.util.Pair;

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

	public void layoutRecipeChain(int startX, int startY, int verticalSpacing, int horizontalSpacing) {
		// Reset rendering flags
		for (IGRecipeNode root : rootNodes) {
			root.resetRender();
		}

		// Step 1: Calculate dimensions
		int maxDepth = calculateMaxDepth();
		int maxWidth = calculateMaxWidth();

		// Create a slightly larger grid to allow for adjustments
		int rows = maxDepth + 1;
		int cols = maxWidth * 2 + 1;  // Extra space for adjustments
		GridCell[][] grid = new GridCell[rows][cols];

		// Initialize grid
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				grid[i][j] = new GridCell();
			}
		}

		// Step 2: Initial placement
		Map<IGRecipeNode, GridPosition> nodePositions = new HashMap<>();
		placeNodesFirstPass(grid, nodePositions);

		// Step 3: Adjust positions to ensure adjacency
		adjustForAdjacency(grid, nodePositions);

		// Step 4: Convert grid positions to coordinates
		for (Map.Entry<IGRecipeNode, GridPosition> entry : nodePositions.entrySet()) {
			IGRecipeNode node = entry.getKey();
			GridPosition pos = entry.getValue();
			node.setX(startX + (pos.col * horizontalSpacing));
			node.setY(startY + (pos.row * verticalSpacing));
		}
	}

	private static class GridCell {
		IGRecipeNode node;
		boolean occupied;

		void clear() {
			node = null;
			occupied = false;
		}

		void place(IGRecipeNode node) {
			this.node = node;
			this.occupied = true;
		}
	}

	private static class GridPosition {
		int row;
		int col;

		GridPosition(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}

	private void placeNodesFirstPass(GridCell[][] grid, Map<IGRecipeNode, GridPosition> nodePositions) {
		int centerCol = grid[0].length / 2;

		// Place each root node and its subtree
		int currentCol = centerCol;
		for (IGRecipeNode root : rootNodes) {
			placeSubtree(root, 0, currentCol, grid, nodePositions, new HashSet<>());
			currentCol += 2;  // Leave space between root trees
		}
	}

	private void placeSubtree(IGRecipeNode node, int row, int col,
							  GridCell[][] grid,
							  Map<IGRecipeNode, GridPosition> nodePositions,
							  Set<IGRecipeNode> visited) {
		if (!visited.add(node) || row >= grid.length || col >= grid[0].length || col < 0) {
			return;
		}

		// Place current node
		grid[row][col].place(node);
		nodePositions.put(node, new GridPosition(row, col));

		// Place children
		List<IGRecipeNode> children = node.getChildren();
		if (children.size() == 1) {
			// Single child - place directly below
			placeSubtree(children.get(0), row + 1, col, grid, nodePositions, visited);
		} else if (children.size() > 1) {
			// Multiple children - distribute them
			int childStartCol = col - children.size() + 1;
			for (IGRecipeNode child : children) {
				placeSubtree(child, row + 1, childStartCol, grid, nodePositions, visited);
				childStartCol += 2;
			}
		}
	}

	private void adjustForAdjacency(GridCell[][] grid, Map<IGRecipeNode, GridPosition> nodePositions) {
		boolean madeAdjustments;
		int maxDepth = 12;
		do {

			madeAdjustments = false;

			// Check each node's position
			for (Map.Entry<IGRecipeNode, GridPosition> entry : new HashMap<>(nodePositions).entrySet()) {
				IGRecipeNode node = entry.getKey();
				GridPosition pos = entry.getValue();

				// Check parent adjacency
				if (!node.getParents().isEmpty() && !hasAdjacentParent(node, grid, pos)) {
					// Try to move node to be adjacent to a parent
					if (adjustNodePosition(node, pos, grid, nodePositions)) {
						madeAdjustments = true;
					}
				}

				// Check child adjacency
				if (!node.getChildren().isEmpty() && !hasAdjacentChild(node, grid, pos)) {
					// Try to move children to be adjacent
					for (IGRecipeNode child : node.getChildren()) {
						GridPosition childPos = nodePositions.get(child);
						if (adjustNodePosition(child, childPos, grid, nodePositions)) {
							madeAdjustments = true;
						}
					}
				}

				if(maxDepth < 0)
				{
					maxDepth = 12;
					madeAdjustments = false;
				}
				maxDepth--;
			}
		} while (madeAdjustments);
	}

	private boolean hasAdjacentParent(IGRecipeNode node, GridCell[][] grid, GridPosition pos) {
		return checkAdjacent(node, grid, pos, node.getParents());
	}

	private boolean hasAdjacentChild(IGRecipeNode node, GridCell[][] grid, GridPosition pos) {
		return checkAdjacent(node, grid, pos, node.getChildren());
	}

	private boolean checkAdjacent(IGRecipeNode node, GridCell[][] grid, GridPosition pos,
								  List<IGRecipeNode> relatives) {
		int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,1}, {-1,-1}, {1,-1}, {1,1}};

		for (int[] dir : directions) {
			int newRow = pos.row + dir[0];
			int newCol = pos.col + dir[1];

			if (isValidPosition(newRow, newCol, grid)) {
				IGRecipeNode adjacent = grid[newRow][newCol].node;
				if (adjacent != null && relatives.contains(adjacent)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean adjustNodePosition(IGRecipeNode node, GridPosition pos,
									   GridCell[][] grid,
									   Map<IGRecipeNode, GridPosition> nodePositions) {
		int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

		// Try each direction
		for (int[] dir : directions) {
			int newRow = pos.row + dir[0];
			int newCol = pos.col + dir[1];

			if (isValidPosition(newRow, newCol, grid) && !grid[newRow][newCol].occupied) {
				// Move node to new position
				grid[pos.row][pos.col].clear();
				grid[newRow][newCol].place(node);
				nodePositions.put(node, new GridPosition(newRow, newCol));
				return true;
			}
		}
		return false;
	}

	private boolean isValidPosition(int row, int col, GridCell[][] grid) {
		return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
	}
	private int calculateMaxDepth() {
		Set<IGRecipeNode> visited = new HashSet<>();
		int maxDepth = 0;

		for (IGRecipeNode root : rootNodes) {
			maxDepth = Math.max(maxDepth, calculateDepthRecursive(root, visited));
		}

		return maxDepth;
	}

	private int calculateDepthRecursive(IGRecipeNode node, Set<IGRecipeNode> visited) {
		if (!visited.add(node)) {
			return 0;
		}

		if (node.getChildren().isEmpty()) {
			return 0;
		}

		int maxChildDepth = 0;
		for (IGRecipeNode child : node.getChildren()) {
			maxChildDepth = Math.max(maxChildDepth, calculateDepthRecursive(child, visited));
		}

		return maxChildDepth + 1;
	}

	private int calculateMaxWidth() {
		// Start with number of root nodes
		int width = rootNodes.size();

		// Add space for branching
		Set<IGRecipeNode> visited = new HashSet<>();
		for (IGRecipeNode root : rootNodes) {
			width = Math.max(width, calculateWidthRecursive(root, visited));
		}

		return width;
	}

	private int calculateWidthRecursive(IGRecipeNode node, Set<IGRecipeNode> visited) {
		if (!visited.add(node)) {
			return 0;
		}

		// Width is at least number of children
		int width = Math.max(1, node.getChildren().size());

		// Add width of all child branches
		for (IGRecipeNode child : node.getChildren()) {
			width = Math.max(width, calculateWidthRecursive(child, visited));
		}

		return width;
	}

	private void placeNodesInGrid(IGRecipeNode[][] grid, Map<IGRecipeNode, GridPosition> nodePositions) {
		// First, place root nodes
		int currentCol = grid[0].length / 2;  // Start in middle column

		for (IGRecipeNode root : rootNodes) {
			placeNodeInGrid(root, 0, currentCol, grid, nodePositions, new HashSet<>());
			currentCol += 2;  // Leave space between root branches
		}
	}

	private void placeNodeInGrid(IGRecipeNode node, int row, int col,
								 IGRecipeNode[][] grid,
								 Map<IGRecipeNode, GridPosition> nodePositions,
								 Set<IGRecipeNode> visited) {
		if (!visited.add(node)) {
			return;
		}

		// Ensure we're within grid bounds
		if (row >= grid.length || col >= grid[0].length || col < 0) {
			return;
		}

		// Place node in grid
		grid[row][col] = node;
		nodePositions.put(node, new GridPosition(row, col));

		// Handle children
		if (node.getChildren().size() == 1) {
			// Single child - place directly below
			placeNodeInGrid(node.getChildren().get(0), row + 1, col,
					grid, nodePositions, visited);
		} else if (node.getChildren().size() > 1) {
			// Multiple children - distribute across columns
			int numChildren = node.getChildren().size();
			int startCol = col - (numChildren - 1);  // Start left of current position

			for (IGRecipeNode child : node.getChildren()) {
				placeNodeInGrid(child, row + 1, startCol,
						grid, nodePositions, visited);
				startCol += 2;  // Leave space between branches
			}
		}
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