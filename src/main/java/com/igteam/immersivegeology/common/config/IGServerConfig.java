/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.config;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.helper.IGConfigurableMachine;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialEvaporateMineral;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
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
	public static final Evaporates EVAPORITES;
	public static final Machines MACHINES;
	public static final VanillaOreRemoval REMOVAL;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		ORES = new IGServerConfig.Ores(builder);
		EVAPORITES = new IGServerConfig.Evaporates(builder);
		MACHINES = new IGServerConfig.Machines(builder);
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
		public final ForgeConfigSpec.BooleanValue shouldRemoveGold;
		public final ForgeConfigSpec.BooleanValue shouldRemoveIEBauxite;
		public final ForgeConfigSpec.BooleanValue shouldRemoveIELead;
		public final ForgeConfigSpec.BooleanValue shouldRemoveIESilver;
		public final ForgeConfigSpec.BooleanValue shouldRemoveIEUranium;
		public final ForgeConfigSpec.BooleanValue shouldRemoveIENickel;
		public final ForgeConfigSpec.BooleanValue logProcess;

		VanillaOreRemoval(ForgeConfigSpec.Builder builder)
		{
			builder.push("remove_non_ig_ore_types").comment("Settings for the configuration of default Ore Spawning for IE and Minecraft, this includes 'Raw Ore Blocks', Reload the world to apply changes");
			shouldRemoveIron = builder.comment("Should IG remove Minecrafts Iron Ore?").define("remove_iron", true);
			shouldRemoveCopper = builder.comment("Should IG remove Minecrafts Copper Ore?").define("remove_copper", true);
			shouldRemoveGold = builder.comment("Should IG remove Minecraft Gold Ore?").define("remove_gold", true);

			shouldRemoveIEBauxite = builder.comment("Should IG remove Immersive Engineering Bauxite Ore?").define("remove_ie_bauxite", true);
			shouldRemoveIELead = builder.comment("Should IG remove Immersive Engineering Lead Ore?").define("remove_ie_lead", true);
			shouldRemoveIESilver = builder.comment("Should IG remove Immersive Engineering Silver Ore?").define("remove_ie_silver", true);
			shouldRemoveIEUranium = builder.comment("Should IG remove Immersive Engineering Uranium Ore?").define("remove_ie_uranium", true);
			shouldRemoveIENickel = builder.comment("Should IG remove Immersive Engineering Nickel Ore?").define("remove_ie_nickel", true);
			logProcess = builder.comment("Should we log the performance of the removal algorithm?").define("log_process", false);
			builder.pop();
		}
	}

	public static class Machines
	{
		public final Map<IGConfigurableMachine, MachineConfig> machines = new HashMap<>();

		Machines(ForgeConfigSpec.Builder builder)
		{
			builder.push("machines");
				for(TemplateMultiblock mb : IGRegistrationHolder.MB_TEMPLATE_MAP.values())
				{
					if(mb instanceof IGConfigurableMachine config)
					{
						machines.put(config, new MachineConfig(builder, config));
					}
				}
			builder.pop();
		}

		public static class MachineConfig
		{
			public final ForgeConfigSpec.IntValue input_batch_size;
			public final ForgeConfigSpec.IntValue default_time;
			public final ForgeConfigSpec.IntValue default_energy;

			public MachineConfig(ForgeConfigSpec.Builder builder, IGConfigurableMachine machine)
			{
				this.input_batch_size = builder.comment("What should the default batch size be for this machine").defineInRange("input_batch_size", machine.getDefaultBatchInput(), 1, 64);
				this.default_energy = builder.comment("The default Total Energy Cost for a Recipe made with this machine").defineInRange("energy", machine.getDefaultEnergy(), 0, 999999);
				this.default_time = builder.comment("he default time for a Recipe to complete with this machine").defineInRange("time", machine.getDefaultTime(), 0, 999999);
			}
		}
	}

	public static class Evaporates
	{
		public final Map<IWorldGenConfig, EvaporateConfig> evaporates = new HashMap<>();
		Evaporates(ForgeConfigSpec.Builder builder)
		{
			builder.push("evaporates");

			for(IWorldGenConfig num : generatedValues())
			{
				try
				{
					this.evaporates.put(num, new EvaporateConfig(builder, num));
				} catch(Exception ex)
				{
					IGLib.IG_LOGGER.info("Exception In Config Creation? {}", ex.getMessage());
				}
			}

			builder.pop();
		}

		private List<IWorldGenConfig> generatedValues()
		{
			List<IWorldGenConfig> list = new ArrayList<>();

			for(MineralEnum m : MineralEnum.values())
			{
				if(m.instance() instanceof MaterialEvaporateMineral)
				{
					list.add(m);
				}
			}

			return list;
		}

		public static class EvaporateConfig
		{
			public final ForgeConfigSpec.DoubleValue density;
			public final ForgeConfigSpec.IntValue veinSize;
			public final ForgeConfigSpec.IntValue minY;
			public final ForgeConfigSpec.IntValue maxY;
			public final ForgeConfigSpec.IntValue veinsPerChunk;
			public final ForgeConfigSpec.IntValue generationChance;
			public final ForgeConfigSpec.IntValue rarity;
			public final ForgeConfigSpec.BooleanValue useSparsePlacement;

			private EvaporateConfig(ForgeConfigSpec.Builder builder, IWorldGenConfig mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.density = builder.comment("how dense is the vein? 0 for all stone, 1 for all ore").defineInRange("density", 0.5, 0.0, 1.0);
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
			List<IWorldGenConfig> v = new ArrayList<>();

			for(MineralEnum m : MineralEnum.values())
			{
				if(!(m.instance() instanceof MaterialEvaporateMineral))
				{
					v.add(m);
				}
			}

			v.addAll(MetalEnum.nativeMetals());
			return v;
		}

		public static class OreConfig
		{
			public final ForgeConfigSpec.BooleanValue canSpawn;
			public final ForgeConfigSpec.DoubleValue density;
			public final ForgeConfigSpec.DoubleValue associateChance;
			public final ForgeConfigSpec.IntValue veinSize;
			public final ForgeConfigSpec.IntValue minY;
			public final ForgeConfigSpec.IntValue maxY;
			public final ForgeConfigSpec.IntValue veinsPerChunk;
			public final ForgeConfigSpec.IntValue generationChance;
			public final ForgeConfigSpec.IntValue rarity;
			public final ForgeConfigSpec.EnumValue<IGGenerationType> generationPattern;
			public final ForgeConfigSpec.BooleanValue useSparsePlacement;
			public final ForgeConfigSpec.DoubleValue min_temp, max_temp, min_downfall, max_downfall;

			private OreConfig(ForgeConfigSpec.Builder builder, IWorldGenConfig mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.canSpawn = builder.comment("Can this Mineral / Ore generate in world, if set to false this mineral will not spawn in world, unless a secondary mineral happens to include it as an assoicate mineral for spawning.").define("canSpawn", true);
				this.density = builder.comment("how dense is the vein? 0 for all stone, 1 for all ore").defineInRange("density", mineral.density(), 0.0, 1.0);
				this.veinSize = builder.comment("The maximum size of a vein. Set to 0 to disable generation").defineInRange("vein_size", mineral.getVeinSize(), 0, Integer.MAX_VALUE);
				this.maxY = builder.comment("The maximum Y coordinate this ore can spawn at").defineInRange("max_y", mineral.getMaxY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.minY = builder.comment("The minimum Y coordinate this ore can spawn at").defineInRange("min_y", mineral.getMinY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.veinsPerChunk = builder.comment("The number of veins attempted to be generated per chunk").defineInRange("attempts_per_chunk", mineral.veinsPerChunk(), 0, Integer.MAX_VALUE);
				this.generationChance = builder.comment("The chance that this mineral is selected for a vein to generate in a chunk, 50000 is a guaranteed spawn 0 prevents spawns").defineInRange("generation_chance",mineral.generationChance(), 0, 50000);
				this.rarity = builder.comment("Controls ore quality distribution. Lower values favor richer ores, while higher values increase the likelihood of poorer ores. 50 is balanced.").defineInRange("rarity",mineral.rarity(), 0, 100);
				this.useSparsePlacement = builder.comment("If enabled, mineral vein will only have a chance to spawn once every [16] chunks on average, inplace of every chunk.").define("useSparsePlacement", mineral.useSparsePlacement());
				this.generationPattern = builder.comment("The type of generation that is used for this mineral").defineEnum("generationPattern", mineral.getGenerationType());
				this.associateChance = builder.comment("The chance that this material will generate with any additional 'friend' materials").defineInRange("associateChance", mineral.getAssociateMaterialChance(), 0.0f, 1.0f);


				this.min_temp = builder.comment("The minimum temperature that this material can spawn in").defineInRange("min_temp", mineral.getMinSpawnTemp(), -2.0f, 2.0f);
				this.max_temp = builder.comment("The maximum temperature that this material can spawn in").defineInRange("max_temp", mineral.getMaxSpawnTemp(), -2.0f, 2.0f);
				this.min_downfall = builder.comment("The minimum downfall that this material can spawn in").defineInRange("min_downfall", mineral.getMinDownfall(), 0.0f, 1.0f);
				this.max_downfall = builder.comment("The maximum downfall that this material can spawn in").defineInRange("max_downfall", mineral.getMaxDownfall(), 0.0f, 1.0f);

				builder.pop();
			}
		}
	}
}
