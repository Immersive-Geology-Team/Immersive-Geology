/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.common.world.noise.INoise3D;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise3D;

public class GenerationTubedNoise implements IGenerationPattern
{
	public INoise3D getiNoise3D(int featureSize, long seed)
	{
		SimplexNoise3D simplex = new SimplexNoise3D(seed);
		SimplexNoise3D warpSimplex = new SimplexNoise3D(seed - 1);

		// Parameters for controlling the distribution of multiple tube centers
		final float xOffsetBase = 12.5f;
		final float zOffsetBase = 12.5f;
		final float tubeSpacing = 25; // Controls how far apart tubes are

		// Warp noise to introduce organic distortions to the tubes
		INoise3D warp = (x, y, z) -> warpSimplex
				.octaves(2, 0.5f)
				.sinWarp(1.5f, 0.8f)
				.flattened(-1, 1)
				.bias(-0.1f)
				.noise((x) / 24, y / 64, (z) / 24);

		return (x, y, z) -> {
			// Generate multiple tube centers with intentional offsets and noise
			// Using random offsets to break the grid structure
			float tubeCenterX = (xOffsetBase + (int)(x / tubeSpacing) * tubeSpacing);  // Adding noise-based offset
			float tubeCenterZ = (zOffsetBase + (int)(z / tubeSpacing) * tubeSpacing);  // Similarly for Z

			// Introduce probability for whether this tube should show up
			float probability = simplex.noise(tubeCenterX / 24, 0, tubeCenterZ / 24); // Noise-based probability
			if (probability < 0.4f) {
				// If probability is low, don't show this tube
				return -1; // Background
			}

			// Compute radial distance from the tube center
			float shiftedX = x - tubeCenterX;
			float shiftedZ = z - tubeCenterZ;
			float r = (float) (Math.sqrt(shiftedX * shiftedX + shiftedZ * shiftedZ) * 1.5f);

			// Base noise pattern for organic tube shape
			float baseNoise = simplex
					.bias(-0.3f)
					.flattened(-1, 1)
					.octaves(3, 0.9f)
					.add(warp)
					.noise(shiftedX / 32, y / 8, shiftedZ / 32);

			// Tube width variation - ensures tube is always defined with some variation
			float tubeVariation = (float) (Math.sin(y / 16.0) * 0.4f + 0.6f);

			// Modify tube density to ensure tubes aren't too small or disappear
			// Use a smaller radius to make sure the tubes are visible, adjust the falloff
			float tubeDensity = (float) Math.exp(-Math.pow((r - 5 + baseNoise * 2) / 4, 2));

			// High contrast: Tubes should stand out, background should be solid -1
			float result = tubeDensity * tubeVariation * 2 - 1; // Scale to -1 to 1 range

			// Only return -1 if it's in the background, otherwise return the tube density result
			return Math.max(-1, result);
		};
	}
}
