/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.config;

import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
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
	public static final VanillaOreRemoval REMOVAL;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		ORES = new IGServerConfig.Ores(builder);
		REMOVAL = new IGServerConfig.VanillaOreRemoval(builder);
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

	public static class VanillaOreRemoval
	{
		public final ForgeConfigSpec.BooleanValue shouldRemoveIron;
		public final ForgeConfigSpec.BooleanValue shouldRemoveCopper;
		VanillaOreRemoval(ForgeConfigSpec.Builder builder)
		{
			builder.push("remove_minecraft_ore_veins").comment("These are the Rare but large veins of minecrafts Copper and Iron (includes Raw Ore Blocks)");
			shouldRemoveIron = builder.comment("Should IG remove Minecrafts Iron Ore Veins?").define("remove_iron", true);
			shouldRemoveCopper = builder.comment("Should IG remove Minecrafts Copper Ore Veins?").define("remove_copper", true);
			builder.pop();
		}
	}

	public static class Ores
	{
		public final Map<IWorldGenConfig, OreConfig> ores = new HashMap<>();
		Ores(ForgeConfigSpec.Builder builder)
		{
			builder.push("ores");

			for(IWorldGenConfig num : generatedValues())
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

		private List<IWorldGenConfig> generatedValues()
		{
			List<IWorldGenConfig> v = new ArrayList<>(Arrays.asList(MineralEnum.values()));
			v.addAll(MetalEnum.nativeMetals());
			return v;
		}

		public static class OreConfig
		{

			public final ForgeConfigSpec.DoubleValue airExposure;
			public final ForgeConfigSpec.IntValue veinSize;
			public final ForgeConfigSpec.IntValue minY;
			public final ForgeConfigSpec.IntValue maxY;
			public final ForgeConfigSpec.IntValue veinsPerChunk;
			public final ForgeConfigSpec.IntValue generationChance;
			public final ForgeConfigSpec.IntValue rarity;
			public final ForgeConfigSpec.BooleanValue useSparsePlacement;

			private OreConfig(ForgeConfigSpec.Builder builder, IWorldGenConfig mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.airExposure = builder.comment("Chance for ores to NOT generate if they are exposed to air. 0 means ignore air exposure, 1 requires being burried.").defineInRange("air_exposure", 0, 0.0, 1.0);
				this.veinSize = builder.comment("The maximum size of a vein. Set to 0 to disable generation").defineInRange("vein_size", mineral.getVeinSize(), 0, Integer.MAX_VALUE);
				this.maxY = builder.comment("The maximum Y coordinate this ore can spawn at").defineInRange("max_y", mineral.getMaxY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.minY = builder.comment("The minimum Y coordinate this ore can spawn at").defineInRange("min_y", mineral.getMinY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.veinsPerChunk = builder.comment("The number of veins attempted to be generated per chunk").defineInRange("attempts_per_chunk", mineral.veinsPerChunk(), 0, Integer.MAX_VALUE);
				this.generationChance = builder.comment("The chance that this mineral is selected for a vein to generate in a chunk, 5000 is a guaranteed spawn 0 prevents spawns").defineInRange("generation_chance",mineral.rarity(), 0, 5000);
				this.rarity = builder.comment("Controls ore quality distribution. Lower values favor richer ores, while higher values increase the likelihood of poorer ores. 50 is balanced.").defineInRange("rarity",mineral.rarity(), 0, 100);
				this.useSparsePlacement = builder.comment("If enabled, mineral vein will only have a chance to spawn once every [16] chunks on average, inplace of every chunk.").define("useSparsePlacement", mineral.useSparsePlacement());
				builder.pop();
			}
		}
	}
}
