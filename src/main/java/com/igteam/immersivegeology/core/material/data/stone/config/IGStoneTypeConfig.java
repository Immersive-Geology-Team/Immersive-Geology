/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class to handle rock types declared via Forge config.
*/
public final class IGStoneTypeConfig
{
	private IGStoneTypeConfig()
	{
	}

	public static final String FILE_NAME = IGLib.MODID+"-stone-types.toml";
	private static final String KEY = "stone";

	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> STONE_TYPES;

	static
	{
		Pair<ForgeConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>>, ForgeConfigSpec> built =
				new ForgeConfigSpec.Builder().configure(IGStoneTypeConfig::build);
		STONE_TYPES = built.getLeft();
		SPEC = built.getRight();
	}

	private static ForgeConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> build(ForgeConfigSpec.Builder builder)
	{
		builder.comment(
				"Rock types from other mods that Immersive Geology should generate ore in.",
				"",
				"One [[declared_stone_types.stone]] block per rock type:",
				"",
				"Example: ",
				"    [[declared_stone_types.stone]]",
				"        block = \"biomesoplenty:brimstone\"",
				"        formation = \"NETHER_STONE\"",
				"        dimensions = [\"minecraft:the_nether\"]",
				"",
				"Required fields:",
				"  block       the block ore replaces",
				"  formation   which minerals accept this rock. One of:",
				"              "+formationNames(),
				"  dimensions  where the rock / stone type can spawn",
				"",
				"Optional fields:",
				"  texture     block texture, defaults to <namespace>:block/<path> of block",
				"  columns     true when the rock has separate top and side textures. Defaults to",
				"              true for SEDIMENTARY, so set false if the rock has only one texture.",
				"  properties  block to copy hardness and sound from, defaults to block",
				"  mod         mod that must be loaded, defaults to block's namespace",
				"  exclude     minerals this rock will not host, for when the rock's own mod",
				"              already has that ore, e.g. exclude = [\"Hematite\"]",
				"",
				"The formation has to be one that minerals allowed in those dimensions accept.",
				"Nether Rocks almost always wants NETHER_STONE: it is what the sulphides minerals use. ",
				"Check the log on start-up as each ",
				"declared stone type will report how many minerals will actually use it.",
				"",
				"Changing this file will only take effect on a restart, as this information needs to be provided at startup"
		).push("declared_stone_types");

		ForgeConfigSpec.ConfigValue<List<? extends UnmodifiableConfig>> value = builder.defineListAllowEmpty(
				List.of(KEY), List::of, entry -> entry instanceof UnmodifiableConfig);

		builder.pop();
		return value;
	}

	public static List<ConfigStoneEntry> load()
	{
		if(DatagenModLoader.isRunningDataGen()) return List.of();

		Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
		if(!Files.isRegularFile(path))
		{

			IGLib.IG_LOGGER.info("No stone type configuration yet. {} will be written during this launch; "+
					"add a rock type to it and restart to generate ore in it.", path);
			return List.of();
		}

		List<UnmodifiableConfig> declared;
		try(Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			UnmodifiableConfig parsed = new TomlParser().parse(reader);
			declared = parsed.getOrElse("declared_stone_types."+KEY, List.of());
		} catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Could not read {}: {}. No declared stone types will be added.", path, e.toString());
			return List.of();
		}

		if(declared==null||declared.isEmpty())
		{
			IGLib.IG_LOGGER.info("No stone types declared in {} - Immersive Geology will generate ore only in its "+
					"own rock types. Add a [[declared_stone_types.stone]] block and restart to add a custom type.", path);
			return List.of();
		}

		List<ConfigStoneEntry> accepted = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		for(UnmodifiableConfig declaration : declared)
		{
			ConfigStoneEntry entry = parse(declaration);
			if(entry==null) continue;
			if(!seen.add(entry.resolvedId.toString()))
			{
				IGLib.IG_LOGGER.error("{}: {} is declared a second time, skipping", FILE_NAME, entry.resolvedId);
				continue;
			}
			accepted.add(entry);
		}

		IGLib.IG_LOGGER.info("Loaded {} declared stone type(s) from {}", accepted.size(), path);
		return List.copyOf(accepted);
	}

	private static final Set<String> KNOWN_KEYS = Set.of(
			"block", "formation", "dimensions", "texture", "columns", "properties", "mod", "exclude");

	private static ConfigStoneEntry parse(UnmodifiableConfig declaration)
	{
		if(declaration==null) return null;

		String label = declaration.getOrElse("block", "<no block>");

		for(String key : declaration.valueMap().keySet())
		{
			if(!KNOWN_KEYS.contains(key.toLowerCase(Locale.ROOT)))
			{
				IGLib.IG_LOGGER.error("{}: rock type \"{}\" has unknown setting \"{}\". Known settings are {}.",
						FILE_NAME, label, key, String.join(", ", new java.util.TreeSet<>(KNOWN_KEYS)));
				return null;
			}
		}

		ConfigStoneEntry entry = new ConfigStoneEntry();
		entry.id = declaration.get("block");
		entry.block = entry.id;
		entry.formation = declaration.get("formation");
		entry.dimensions = strings(declaration, "dimensions");
		entry.texture = declaration.get("texture");
		entry.properties_from = declaration.get("properties");
		entry.requires_mod = declaration.get("mod");
		entry.excluded_minerals = strings(declaration, "exclude");

		Object columns = declaration.get("columns");
		if(columns instanceof Boolean flag) entry.sedimentary_textures = flag;
		else if(columns!=null)
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" has columns = {}, which must be true or false",
					FILE_NAME, label, columns);
			return null;
		}

		if(entry.id==null||entry.id.isBlank())
		{
			IGLib.IG_LOGGER.error("{}: a rock type has no block. Every entry needs block, formation and dimensions.",
					FILE_NAME);
			return null;
		}
		if(entry.formation==null||entry.formation.isBlank())
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" has no formation. Expected one of {}",
					FILE_NAME, label, formationNames());
			return null;
		}
		if(entry.dimensions==null||entry.dimensions.isEmpty())
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" names no dimensions. "+
					"Ore only generates where the rock is declared to be.", FILE_NAME, label);
			return null;
		}

		return validate(entry, label)?entry: null;
	}

	private static List<String> strings(UnmodifiableConfig declaration, String key)
	{
		Object raw = declaration.get(key);
		if(raw==null) return null;
		if(raw instanceof String single) return List.of(single);
		if(raw instanceof List<?> list)
		{
			List<String> values = new ArrayList<>();
			for(Object value : list) if(value!=null) values.add(value.toString());
			return values;
		}
		return null;
	}

	private static boolean validate(ConfigStoneEntry entry, String label)
	{
		entry.resolvedId = parseId(entry.id, label, "block");
		if(entry.resolvedId==null) return false;

		String blockId = entry.block!=null&&!entry.block.isBlank()?entry.block: entry.id;
		entry.resolvedBlock = parseId(blockId, label, "block");
		if(entry.resolvedBlock==null) return false;

		try
		{
			entry.resolvedFormation = StoneFormation.valueOf(entry.formation.toUpperCase(Locale.ROOT));
		} catch(IllegalArgumentException e)
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" has formation \"{}\", which is not one of {}",
					FILE_NAME, label, entry.formation, formationNames());
			return false;
		}

		Set<ResourceLocation> dimensions = new LinkedHashSet<>();
		for(String dimension : entry.dimensions)
		{
			if(dimension.isBlank()) continue;
			ResourceLocation parsed = parseId(dimension, label, "dimension");
			if(parsed==null) return false;
			dimensions.add(parsed);
		}
		if(dimensions.isEmpty())
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" names no dimensions.", FILE_NAME, label);
			return false;
		}
		entry.resolvedDimensions = Set.copyOf(dimensions);

		entry.resolvedMod = entry.requires_mod!=null&&!entry.requires_mod.isBlank()
				?entry.requires_mod
				: entry.resolvedId.getNamespace();

		entry.resolvedTexture = entry.texture!=null&&!entry.texture.isBlank()
				?parseId(entry.texture, label, "texture")
				: new ResourceLocation(entry.resolvedBlock.getNamespace(), "block/"+entry.resolvedBlock.getPath());
		if(entry.resolvedTexture==null) return false;

		entry.resolvedProperties = entry.properties_from!=null&&!entry.properties_from.isBlank()
				?parseId(entry.properties_from, label, "properties")
				: entry.resolvedBlock;
		if(entry.resolvedProperties==null) return false;

		entry.resolvedColumns = entry.sedimentary_textures!=null
				?entry.sedimentary_textures
				: entry.resolvedFormation==StoneFormation.SEDIMENTARY;

		Set<String> exclusions = new HashSet<>();
		if(entry.excluded_minerals!=null)
		{
			for(String mineral : entry.excluded_minerals)
			{
				if(mineral!=null&&!mineral.isBlank()) exclusions.add(mineral.trim().toLowerCase(Locale.ROOT));
			}
		}
		entry.resolvedExclusions = Set.copyOf(exclusions);

		return true;
	}

	private static ResourceLocation parseId(String raw, String label, String field)
	{
		ResourceLocation parsed = raw==null?null: ResourceLocation.tryParse(raw.trim());
		if(parsed==null)
		{
			IGLib.IG_LOGGER.error("{}: rock type \"{}\" has {} \"{}\", which is not a valid namespaced id",
					FILE_NAME, label, field, raw);
		}
		return parsed;
	}

	private static String formationNames()
	{
		StringBuilder names = new StringBuilder();
		for(StoneFormation formation : StoneFormation.values())
		{
			if(!names.isEmpty()) names.append(", ");
			names.append(formation.name());
		}
		return names.toString();
	}
}
