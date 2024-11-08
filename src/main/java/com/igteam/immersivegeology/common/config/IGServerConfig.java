/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.config;

import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.config.IEServerConfig.Ores;
import blusunrize.immersiveengineering.common.config.IEServerConfig.Ores.OreDistribution;
import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.world.MineralCombination;
import com.igteam.immersivegeology.common.world.MineralEntry;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;

@EventBusSubscriber(modid = IGLib.MODID, bus = Bus.MOD)
public class IGServerConfig
{
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final Ores ORES;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		ORES = new IGServerConfig.Ores(builder);
		CONFIG_SPEC = builder.build();
	}

	private static Config rawConfig;

	public static Config getRawConfig()
	{
		return Preconditions.checkNotNull(rawConfig);
	}

	@SubscribeEvent
	public static void onConfigReload(ModConfigEvent ev)
	{
		if(CONFIG_SPEC==ev.getConfig().getSpec())
		{
			rawConfig = ev.getConfig().getConfigData();
		}
	}

	public static int getOrDefault(IntValue value)
	{
		return CONFIG_SPEC.isLoaded()?value.get(): value.getDefault();
	}

	public static class Ores
	{
		public final Map<MineralEnum, OreConfig> ores = new HashMap<>();
		Ores(ForgeConfigSpec.Builder builder)
		{
			builder.push("ores");

			for(MineralEnum num : MineralEnum.values())
			{
				try
				{
					this.ores.put(num, new OreConfig(builder, num));
				} catch(Exception ex)
				{
					IGLib.IG_LOGGER.info("Exception In Config Creation? {}", ex.getMessage());
				}
			}

			builder.pop();
		}

		public static class OreConfig
		{

			public final ForgeConfigSpec.DoubleValue airExposure;
			public final ForgeConfigSpec.IntValue veinSize;
			public final ForgeConfigSpec.IntValue minY;
			public final ForgeConfigSpec.IntValue maxY;
			public final ForgeConfigSpec.IntValue veinsPerChunk;
			public final ForgeConfigSpec.IntValue generationChance;

			private OreConfig(ForgeConfigSpec.Builder builder, MineralEnum mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.airExposure = builder.comment("Chance for ores to NOT generate if they are exposed to air. 0 means ignore air exposure, 1 requires being burried.").defineInRange("air_exposure", 0, 0.0, 1.0);
				this.veinSize = builder.comment("The maximum size of a vein. Set to 0 to disable generation").defineInRange("vein_size", mineral.getVeinSize(), 0, Integer.MAX_VALUE);
				this.maxY = builder.comment("The maximum Y coordinate this ore can spawn at").defineInRange("max_y", mineral.getMaxY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.minY = builder.comment("The minimum Y coordinate this ore can spawn at").defineInRange("min_y", mineral.getMinY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.veinsPerChunk = builder.comment("The number of veins attempted to be generated per chunk").defineInRange("attempts_per_chunk", mineral.veinsPerChunk(), 0, Integer.MAX_VALUE);
				this.generationChance = builder.comment("").defineInRange("generation_chance",mineral.rarity(), 0, 100);
				builder.pop();
			}
		}
	}
}
