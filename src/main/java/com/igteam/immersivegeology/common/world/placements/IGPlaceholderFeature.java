/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.placements;

import com.igteam.immersivegeology.common.world.placements.IGPlaceholderFeature.IGPlaceholderFeatureConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class IGPlaceholderFeature extends Feature<IGPlaceholderFeatureConfig>
{
	public IGPlaceholderFeature(){
		super(IGPlaceholderFeature.IGPlaceholderFeatureConfig.CODEC.codec());
	}

	@Override
	public boolean place(FeaturePlaceContext<IGPlaceholderFeatureConfig> ctx)
	{
		return false;
	}

	public record IGPlaceholderFeatureConfig() implements FeatureConfiguration
	{
		public static final MapCodec<IGPlaceholderFeatureConfig> CODEC = MapCodec.unit(IGPlaceholderFeatureConfig::new);
	}
}
