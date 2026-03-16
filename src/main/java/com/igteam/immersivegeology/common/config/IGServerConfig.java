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
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialEvaporateMineral;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.resources.ResourceLocation;
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

	public static final ForgeConfigSpec.BooleanValue disable_mineral_generation;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("global_settings").comment("Configure global server settings.");
		disable_mineral_generation = builder.comment("Remove IG's Ore Generation in full.").define("disable_mineral_generation", false);
		builder.pop();
		MACHINES = new IGServerConfig.Machines(builder);
		REMOVAL = new IGServerConfig.VanillaOreRemoval(builder);
		EVAPORITES = new IGServerConfig.Evaporates(builder);
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
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> biome_blacklist;

		VanillaOreRemoval(ForgeConfigSpec.Builder builder)
		{
			builder.push("remove_non_ig_ore_types").comment("Configure which default ores (from Minecraft and Immersive Engineering) should be removed during world generation. Changes require a world reload.");
			shouldRemoveIron = builder.comment("Remove Minecraft's Iron Ore during world generation.").define("remove_iron", true);
			shouldRemoveCopper = builder.comment("Remove Minecraft's Copper Ore during world generation.").define("remove_copper", true);
			shouldRemoveGold = builder.comment("Remove Minecraft's Gold Ore during world generation.").define("remove_gold", true);

			shouldRemoveIEBauxite = builder.comment("Remove Immersive Engineering's Bauxite Ore.").define("remove_ie_bauxite", true);
			shouldRemoveIELead = builder.comment("Remove Immersive Engineering's Lead Ore.").define("remove_ie_lead", true);
			shouldRemoveIESilver = builder.comment("Remove Immersive Engineering's Silver Ore.").define("remove_ie_silver", true);
			shouldRemoveIEUranium = builder.comment("Remove Immersive Engineering's Uranium Ore.").define("remove_ie_uranium", true);
			shouldRemoveIENickel = builder.comment("Remove Immersive Engineering's Nickel Ore.").define("remove_ie_nickel", true);
			logProcess = builder.comment("Enable logging of the ore removal process for debugging or performance analysis.").define("log_process", false);

			builder.comment("Specify biomes where ore removal should be disabled. Useful for preserving ore generation in specific areas without disabling the system entirely.");
			this.biome_blacklist = builder.comment("List of biome tags (e.g., 'minecraft:is_overworld', 'forge:is_sandy', 'forge:is_dry/overworld', 'minecraft:spawns_warm_variant_frogs', or 'minecraft:has_structure/mineshaft_mesa') where ore removal will be skipped. Applies to all ore types defined above.").defineListAllowEmpty("biome_blacklist",
					List.of(),
					obj -> obj instanceof String && ResourceLocation.isValidResourceLocation((String) obj)
			);
			builder.pop();
		}
	}

	public static class Machines
	{
		public final Map<String, MachineConfig> machines = new HashMap<>();

		Machines(ForgeConfigSpec.Builder builder)
		{
			builder.push("machines").comment("=== IG Machine Config Start ===");
				for(TemplateMultiblock mb : IGRegistrationHolder.MB_TEMPLATE_MAP.values())
				{
					if(mb instanceof IGConfigurableMachine config)
					{
						String mb_name = config.getName().toLowerCase().replace(' ', '_');
						builder.push(mb_name);
						machines.put(mb_name, new MachineConfig(builder, config));
						builder.pop();
					}
				}
			builder.pop();
		}

		public static class MachineConfig
		{
			public final ForgeConfigSpec.IntValue default_skin_ordinal;

			public MachineConfig(ForgeConfigSpec.Builder builder, IGConfigurableMachine machine)
			{
				this.default_skin_ordinal = builder.comment("The index number for the default skin an IG multiblock will use").defineInRange("default_skin_ordinal", machine.getDefaultSkin(), 0, 99);
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
			public final ForgeConfigSpec.BooleanValue disabled;

			private EvaporateConfig(ForgeConfigSpec.Builder builder, IWorldGenConfig mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.disabled = builder.comment("Enable or Disable this Mineral").define("disabled", false);
				this.density = builder.comment("how dense is the vein? 0 for all stone, 1 for all ore").defineInRange("density", 0.5, 0.0, 1.0);
				this.veinSize = builder.comment("The maximum size of a vein. Set to 0 to disable generation").defineInRange("vein_size", mineral.getVeinSize(), 0, Integer.MAX_VALUE);
				this.maxY = builder.comment("The maximum Y coordinate this ore can spawn at").defineInRange("max_y", mineral.getMaxY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.minY = builder.comment("The minimum Y coordinate this ore can spawn at").defineInRange("min_y", mineral.getMinY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
				this.veinsPerChunk = builder.comment("The number of times a vein attempts to generate").defineInRange("attempts_per_chunk", mineral.veinsPerChunk(), 0, Integer.MAX_VALUE);
				this.generationChance = builder.comment("The chance that this mineral is generated in a chunk, 2_000_000 is guaranteed spawn 0 prevents spawns").defineInRange("generation_chance",mineral.rarity(), 0, 2_000_000);
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
			public final ForgeConfigSpec.ConfigValue<List<? extends String>> dimension_whitelist;

			private OreConfig(ForgeConfigSpec.Builder builder, IWorldGenConfig mineral)
			{
				builder.comment("Ore Generation Config - "+mineral.name()).push(mineral.name());
				this.canSpawn = builder.comment("Can this Mineral / Ore generate in world, if set to false this mineral will not spawn in world, unless a secondary mineral happens to include it as an associate mineral for spawning.").define("canSpawn", true);
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

				this.dimension_whitelist = builder.comment("A List of dimensions that this ore can generate in.").defineListAllowEmpty("dimension_whitelist",
						mineral.getDefaultDimensions(),
						obj -> obj instanceof String && ResourceLocation.isValidResourceLocation((String) obj)
				);

				builder.pop();
			}
		}
	}
}
