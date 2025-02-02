/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.features.helper;

import com.igteam.immersivegeology.common.world.features.IGOreFeature;

public enum IGGenerationType
{
	DEFAULT(new GenerationDefaultNoise()),
	BANDED(new GenerationBandedNoise()),
	EVAPORATE(new GenerationDefaultNoise()),
	TUBE(new GenerationTubedNoise());

	final IGenerationPattern pattern;

	IGGenerationType(IGenerationPattern pattern)
	{
		this.pattern = pattern;
	}

	public IGenerationPattern getPattern()
	{
		return pattern;
	}
}
