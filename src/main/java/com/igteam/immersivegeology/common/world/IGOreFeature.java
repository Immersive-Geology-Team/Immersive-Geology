/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.IGOreFeature.IGOreFeatureConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;

import java.util.List;
import java.util.Optional;

public class IGOreFeature extends Feature<IGOreFeatureConfig>
{
	public IGOreFeature(){
		super(IGOreFeature.IGOreFeatureConfig.CODEC);
	}

	public boolean place(FeaturePlaceContext<IGOreFeatureConfig> ctx) {
		IGOreFeatureConfig config = ctx.config();
		OreConfiguration vanillaConfig = new OreConfiguration(config.targetList(), config.getSize(), (float)config.getAirExposure());
		return Feature.ORE.place(new FeaturePlaceContext<>(Optional.empty(), ctx.level(), ctx.chunkGenerator(), ctx.random(), ctx.origin(), vanillaConfig));
	}

	public record IGOreFeatureConfig(List<TargetBlockState> targetList, MineralEntry type) implements FeatureConfiguration
	{
		public static final Codec<IGOreFeatureConfig> CODEC = RecordCodecBuilder.create((app) -> {
			return app.group(
					Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter((cfg) -> cfg.targetList),
					MineralEntry.CODEC.fieldOf("type").forGetter((cfg) -> cfg.type)
			).apply(app, IGOreFeatureConfig::new);
		});

		public int getSize() {
			return IGServerConfig.ORES.ores.get(type).veinSize.get();
		}

		public double getAirExposure() {
			return IGServerConfig.ORES.ores.get(type).airExposure.get();
		}

		public List<OreConfiguration.TargetBlockState> targetList() {
			return this.targetList;
		}

		public MineralEntry type() {
			return this.type;
		}
	}
}
