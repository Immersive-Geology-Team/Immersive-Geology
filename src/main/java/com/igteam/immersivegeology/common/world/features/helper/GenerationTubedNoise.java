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

		float xOffset = 50;
		float zOffset = 50;

		// Warp noise generator for band distortion
		INoise3D warp = (x, y, z) -> warpSimplex
				.octaves(3, 0.6f)  // More octaves for finer details
				.sinWarp(2, 1)
				.flattened(-1, 1)
				.bias(-0.2f)
				.noise((x - xOffset) / 24, y / 48, (z - zOffset) / 24);  // Slightly stretched in Y for subtle distortion


		// Primary noise generator
		return (x, y, z) -> {
			float shiftedX = (x - xOffset);
			float shiftedZ = (z - zOffset);

			float r = (float) Math.sqrt((shiftedX * shiftedX) + (shiftedZ * shiftedZ));

			float baseNoise = simplex
					.bias(-0.4f)
					.flattened(-1,1)
					.octaves(4, 1f)
					.add(warp)
					.noise(shiftedX / 32, y / 4, shiftedZ / 32);

			float tubeVariation = (float)((Math.sin(y / 16) * 0.4f) + 0.6f);
			float tubeDensity = (float) Math.exp(-Math.pow((r - 8 + baseNoise * 4) / 6, 2)) * 0.5f;

			// Combine the base noise with the band effect
			return Math.max(-1,(tubeDensity * tubeVariation * 2 - 1)); // Apply the Y-axis fading to the overall noise
		};
	}
}
