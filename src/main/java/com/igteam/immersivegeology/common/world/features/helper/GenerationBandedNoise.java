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

		// Warp noise generator for subtle band distortion
		INoise3D warp = (x, y, z) -> warpSimplex
				.bias(-0.6f)
				.octaves(2, 0.7f)  // Fewer octaves, reducing finer distortion
				.sinWarp(1.5f, 0.75f)  // Lower warping intensity
				.flattened(-0.8f, 0.8f)  // Slightly lower amplitude
				.noise(x / 80, y / 50, z / 80);  // Gentle stretch in Y-axis

		// Primary noise generator
		return (x, y, z) -> {
			// Base noise with slight Y stretch for banding
			float baseNoise = simplex
					.flattened(-1, 1)
					.octaves(3, 0.9f)
					.add(warp)  // Apply the warp to distort layers
					.noise(x / featureSize, y / 10, z / featureSize); // Stretched Y for more controlled banding

			// More structured banding with less erratic warping
			float bandEffect = (float) Math.sin(
					(y / (5.5f + warp.noise(x, y, z) * 0.2f))  // Less influence from warp
							- (warp.noise(x, y, z) * 0.15f)  // Reduce phase shift from warp
			);

			return (baseNoise * bandEffect) * -1; // Apply banding effect to noise
		};
	}
}
