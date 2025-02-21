/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import java.util.*;

public class IGGraphLayoutManager {
	private final Map<Integer, Map<Integer, IGRecipeNode>> grid = new HashMap<>();
	private final Map<IGRecipeNode, int[]> positions = new HashMap<>();
	private final Set<IGRecipeNode> processedNodes = new HashSet<>();

	public void layoutChain(IGRecipeChain chain) {
		// Reset the layout data
		grid.clear();
		positions.clear();
		processedNodes.clear();

		// Get all nodes and identify SCCs (Strongly Connected Components)
		List<Set<IGRecipeNode>> sccs = findStronglyConnectedComponents(chain);

		// Assign layers to nodes
		Map<IGRecipeNode, Integer> layers = assignLayers(chain.getRootNodes(), sccs);

		// Place nodes on grid
		placeNodesOnGrid(chain.getRootNodes(), layers, sccs);

		// Apply final positions to nodes
		for (Map.Entry<IGRecipeNode, int[]> entry : positions.entrySet()) {
			IGRecipeNode node = entry.getKey();
			int[] pos = entry.getValue();
			node.setX(pos[0]);
			node.setY(pos[1]);
		}
	}

	private List<Set<IGRecipeNode>> findStronglyConnectedComponents(IGRecipeChain chain) {
		Stack<IGRecipeNode> stack = new Stack<>();
		Set<IGRecipeNode> visited = new HashSet<>();
		List<Set<IGRecipeNode>> sccs = new ArrayList<>();

		// First DFS to fill stack
		for (IGRecipeNode root : chain.getRootNodes()) {
			if (!visited.contains(root)) {
				dfsFirst(root, visited, stack);
			}
		}

		// Reset visited for second DFS
		visited.clear();

		// Second DFS to find SCCs
		while (!stack.isEmpty()) {
			IGRecipeNode node = stack.pop();
			if (!visited.contains(node)) {
				Set<IGRecipeNode> scc = new HashSet<>();
				dfsSecond(node, visited, scc);
				sccs.add(scc);
			}
		}

		return sccs;
	}

	private void dfsFirst(IGRecipeNode node, Set<IGRecipeNode> visited, Stack<IGRecipeNode> stack) {
		visited.add(node);
		for (IGRecipeNode child : node.getChildren()) {
			if (!visited.contains(child)) {
				dfsFirst(child, visited, stack);
			}
		}
		stack.push(node);
	}

	private void dfsSecond(IGRecipeNode node, Set<IGRecipeNode> visited, Set<IGRecipeNode> scc) {
		visited.add(node);
		scc.add(node);
		for (IGRecipeNode parent : node.getParents()) {
			if (!visited.contains(parent)) {
				dfsSecond(parent, visited, scc);
			}
		}
	}

	private Map<IGRecipeNode, Integer> assignLayers(List<IGRecipeNode> rootNodes, List<Set<IGRecipeNode>> sccs) {
		Map<IGRecipeNode, Integer> layers = new HashMap<>();
		Map<IGRecipeNode, Set<IGRecipeNode>> sccMap = new HashMap<>();

		// Build SCC lookup map
		for (Set<IGRecipeNode> scc : sccs) {
			for (IGRecipeNode node : scc) {
				sccMap.put(node, scc);
			}
		}

		// Assign layers starting from roots
		for (IGRecipeNode root : rootNodes) {
			assignLayer(root, 0, layers, sccMap);
		}

		return layers;
	}

	private int assignLayer(IGRecipeNode node, int currentLayer,
							Map<IGRecipeNode, Integer> layers,
							Map<IGRecipeNode, Set<IGRecipeNode>> sccMap) {
		if (layers.containsKey(node)) {
			return layers.get(node);
		}

		layers.put(node, currentLayer);

		// If node is part of SCC, assign same layer to all nodes in component
		if (sccMap.containsKey(node)) {
			Set<IGRecipeNode> scc = sccMap.get(node);
			if (scc.size() > 1) {
				for (IGRecipeNode sccNode : scc) {
					layers.put(sccNode, currentLayer);
				}
			}
		}

		// Process children
		int maxChildLayer = currentLayer;
		for (IGRecipeNode child : node.getChildren()) {
			if (!sccMap.getOrDefault(node, Collections.emptySet()).contains(child)) {
				int childLayer = assignLayer(child, currentLayer + 1, layers, sccMap);
				maxChildLayer = Math.max(maxChildLayer, childLayer);
			}
		}

		return maxChildLayer;
	}

	private void placeNodesOnGrid(List<IGRecipeNode> rootNodes,
								  Map<IGRecipeNode, Integer> layers,
								  List<Set<IGRecipeNode>> sccs) {
		// Group nodes by layer
		Map<Integer, List<IGRecipeNode>> nodesByLayer = new HashMap<>();
		layers.forEach((node, layer) -> {
			nodesByLayer.computeIfAbsent(layer, k -> new ArrayList<>()).add(node);
		});

		// Place nodes layer by layer
		for (int layer : nodesByLayer.keySet().stream().sorted().toList()) {
			List<IGRecipeNode> nodes = nodesByLayer.get(layer);

			// Sort nodes to minimize edge crossings
			nodes.sort((a, b) -> {
				double xA = calculateOptimalX(a);
				double xB = calculateOptimalX(b);
				return Double.compare(xA, xB);
			});

			// Place nodes on grid
			for (IGRecipeNode node : nodes) {
				if (!positions.containsKey(node)) {
					int optimalX = (int) Math.round(calculateOptimalX(node));
					int x = findNearestAvailableX(optimalX, layer);
					positions.put(node, new int[]{x, layer});
					grid.computeIfAbsent(layer, k -> new HashMap<>()).put(x, node);
				}
			}
		}
	}

	private double calculateOptimalX(IGRecipeNode node) {
		List<Integer> connectedX = new ArrayList<>();

		// Consider positions of connected nodes (both parents and children)
		for (IGRecipeNode parent : node.getParents()) {
			if (positions.containsKey(parent)) {
				connectedX.add(positions.get(parent)[0]);
			}
		}

		for (IGRecipeNode child : node.getChildren()) {
			if (positions.containsKey(child)) {
				connectedX.add(positions.get(child)[0]);
			}
		}

		if (connectedX.isEmpty()) {
			return positions.size(); // Place at the end
		}

		// Return average x position of connected nodes
		return connectedX.stream().mapToInt(Integer::intValue).average().orElse(0);
	}

	private int findNearestAvailableX(int optimalX, int layer) {
		int x = optimalX;
		int step = 1;
		Map<Integer, IGRecipeNode> layerGrid = grid.getOrDefault(layer, new HashMap<>());

		// Search alternately left and right until an empty position is found
		while (true) {
			if (!layerGrid.containsKey(x)) {
				return x;
			}
			x += step;
			step = -step;
			if (step > 0) {
				step++;
			}
		}
	}
}