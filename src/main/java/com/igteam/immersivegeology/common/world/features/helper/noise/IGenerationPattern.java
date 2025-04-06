/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper.noise;

import com.igteam.immersivegeology.common.world.noise.INoise3D;

public interface IGenerationPattern
{
	INoise3D getiNoise3D(int featureSize, long seed);
}
