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

public class GenerationBandedNoise implements IGenerationPattern
{
	public INoise3D getiNoise3D(int featureSize, long seed)
	{
		SimplexNoise3D simplex = new SimplexNoise3D(seed);
		SimplexNoise3D warpSimplex = new SimplexNoise3D(seed - 1);

		// Warp noise generator for band distortion
		INoise3D warp = (x, y, z) -> warpSimplex
				.octaves(3, 0.8f)  // More octaves for finer details
				.sinWarp(2, 1)     // Add sine warping to create wave-like distortions
				.flattened(-1, 1)
				.bias(-0.3f)
				.noise(x / 72, y / 48, z / 72);  // Slightly stretched in Y for subtle distortion

		// Primary noise generator
		return (x, y, z) -> {
			// Apply the base noise with a stretch on Y for a more banded effect
			float baseNoise = simplex
					.bias(-0.5f)
					.flattened(-1, 1)
					.octaves(3, 1f)
					.add(warp)  // Apply the warp to distort layers
					.noise(x / featureSize, y / 8, z / featureSize); // Stretched Y-axis for more subtle variations

			// Create a band-like effect on the Y-axis by applying a sine function
			// The sine function causes a smooth fade in and out along the Y-axis
			float bandEffect = (float) Math.sin(y / (5 * (1.25f+warp.noise(x,y,z) * 0.25f)) - (warp.noise(x,y,z) * 0.25f)); // Smooth fading in and out along Y

			// Combine the base noise with the band effect
			return (baseNoise * bandEffect) * -1; // Apply the Y-axis fading to the overall noise
		};
	}
}
