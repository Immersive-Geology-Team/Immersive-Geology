/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper.noise;

import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise3D;

public class GenerationTubedNoise implements IGenerationPattern
{
	public INoise3D getiNoise3D(int featureSize, long seed)
	{
		// Create a warp noise generator for organic twist (used later).
		SimplexNoise3D warpSimplex = new SimplexNoise3D(seed - 1);
		INoise3D warp = (x, y, z) -> warpSimplex
				.octaves(2, 0.5f)
				.sinWarp(1.5f, 0.8f)
				.flattened(-1, 1)
				.bias(-0.1f)
				.noise(x / 24, y / 64, z / 24);

		// Parameters for the cellular distribution of tube seeds.
		final float tubeCellSize  = Math.max(20, 50 - featureSize);
		final float wallThickness = 5.0f;     // Total thickness of the tube wall.
		final float falloff     = 1.0f;       // Smoothing distance at the edges of the tube wall.

		// Create a separate noise generator to drive the wobble of the tube seeds.
		SimplexNoise3D wobbleSimplex = new SimplexNoise3D(seed + 1000);
		INoise3D wobbleNoise = (x, y, z) -> wobbleSimplex
				.octaves(1, 0.7f)
				.flattened(-1, 1)
				.noise(x, y, z);

		return (x, y, z) -> {

			// For hollow tubes, define inner and outer boundaries.
			final float tubeMidRadius = 8.0f * (0.75f + (0.75f * warp.noise(x,y,z)));
			float innerEdge = tubeMidRadius - wallThickness * 0.5f;
			float outerEdge = tubeMidRadius + wallThickness * 0.5f;

			// --- 1. Compute Cellular Distance (in XZ) with Wobble ---
			// Determine which cell (of size tubeCellSize) the point is in.
			int cellX = (int) Math.floor(x / tubeCellSize);
			int cellZ = (int) Math.floor(z / tubeCellSize);
			float minDist = Float.MAX_VALUE;
			// Check the current cell and its 8 neighbors.
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					int neighborX = cellX + i;
					int neighborZ = cellZ + j;
					// Each cell gets a random offset (in [0, tubeCellSize)) for the tube seed.
					float offsetX = pseudoRandom(neighborX, neighborZ, seed) * tubeCellSize;
					float offsetZ = pseudoRandom(neighborX, neighborZ, seed + 1) * tubeCellSize;

					// Base candidate center for the cell.
					float baseCenterX = neighborX * tubeCellSize + offsetX;
					float baseCenterZ = neighborZ * tubeCellSize + offsetZ;

					// --- Apply Wobble ---
					// Compute an additional offset based on the candidate center and the vertical position.
					// The noise here is evaluated at a scaled version of the candidate center plus y, ensuring a smooth variation.
					float wobbleAmount = 3.0f; // Maximum displacement for the wobble effect.
					float wobbleX = wobbleNoise.noise(baseCenterX / 50.0f, y / 50.0f, baseCenterZ / 50.0f) * wobbleAmount;
					float wobbleZ = wobbleNoise.noise(baseCenterX / 50.0f, y / 50.0f, baseCenterZ / 50.0f + 100) * wobbleAmount;

					// The final candidate center is the base center plus the wobble offsets.
					float candidateCenterX = baseCenterX + wobbleX;
					float candidateCenterZ = baseCenterZ + wobbleZ;

					// Compute the distance from the current (x,z) point to this candidate tube seed.
					float dx = x - candidateCenterX;
					float dz = z - candidateCenterZ;
					float d = (float) Math.sqrt(dx * dx + dz * dz);
					if (d < minDist) {
						minDist = d;
					}
				}
			}

			// --- 2. Convert Distance to Hollow Tube Density ---
			// We want density only in a narrow band:
			//   * Inside the inner edge, we are hollow (density = 0).
			//   * Between innerEdge and innerEdge+falloff, density rises from 0 to 1.
			//   * Between innerEdge+falloff and outerEdge-falloff, density stays at 1.
			//   * Between outerEdge-falloff and outerEdge, density falls from 1 to 0.
			//   * Outside outerEdge, density is 0.
			float densityInner  = smoothStep(innerEdge, innerEdge + falloff, minDist);
			float densityOuter  = smoothStep(outerEdge - falloff, outerEdge, minDist);
			float tubeDensity = densityInner - densityOuter;

			// --- 3. Vertical Variation ---
			// Modulate the density with a sine function so that tubes twist and vary with height.
			float verticalVariation = (float)(Math.sin(y / 16.0) * 0.2f) + 1f + (0.5f * warp.noise(x,y,z));

			// --- 4. Apply a Normalized Warp for Extra Organic Variation ---
			// Normalize the warp noise to [0,1] to avoid negative scaling.
			float twist = warp.noise(x / 32, y / 8, z / 32) + 1f;

			// Combine the factors.
			float product = tubeDensity * verticalVariation * twist;

			// --- 5. Final Contrast ---
			// Scale so that tube regions approach +1 and the background remains at -1.
			float result = product * 2 - 1;
			return result;
		};
	}


	/**
	 * A smoothstep function that transitions smoothly from 0 to 1 over the interval [edge0, edge1].
	 */
	private float smoothStep(float edge0, float edge1, float x) {
		float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
		return t * t * (3 - 2 * t);
	}

	/**
	 * Clamps a value between a minimum and a maximum.
	 */
	private float clamp(float x, float min, float max) {
		return Math.max(min, Math.min(max, x));
	}

	/**
	 * A simple pseudo-random hash function that produces a float in [0,1) from integer coordinates.
	 */
	private float pseudoRandom(int x, int z, long seed) {
		int n = x * 374761393 + z * 668265263 + (int)seed * 15485863;
		n = (n ^ (n >> 13)) * 1274126177;
		n = n ^ (n >> 16);
		// Use & with 0x7fffffff to ensure non-negative and then normalize.
		return (n & 0x7fffffff) / (float)0x7fffffff;
	}

}
