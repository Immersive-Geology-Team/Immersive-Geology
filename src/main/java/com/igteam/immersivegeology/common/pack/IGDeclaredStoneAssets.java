/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.ore.IGWeatheringOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.stone.config.ConfigMaterialStone;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Builds, at runtime, every resource an ore block hosted in a configuration-declared rock type needs.
 * <p>
 * Built-in rock types have all of this from data generation, which comes precompiled. A rock type someone
 * declares in their configuration is only known once the game starts, so the same files have to be generated live instead
 * So we create new blockstates, models, loot table, tags and name here now.
 * {@link IGDeclaredStonePack} is what's used to translate this data.
 * <p>
 * The structure is deliberately using my {@code IGBlockStateProvider}, {@code IGItemModelProvider},
 * {@code IGBlockLootProvider} and {@code IGBlockTags} emit for the built-in rock, so a declared rock type behave should
 * blend in without issue.
 */
public final class IGDeclaredStoneAssets
{
	private IGDeclaredStoneAssets()
	{
	}

	private static final int[] VARIATIONS = {1, 2};

	public static final class Assets
	{
		public final Map<ResourceLocation, byte[]> client = new LinkedHashMap<>();
		public final Map<ResourceLocation, byte[]> server = new LinkedHashMap<>();
		public int oreBlocks;
		public int stoneTypes;
	}

	private static Assets shared;

	/** Built once, shared by the client and server packs. */
	public static synchronized Assets shared()
	{
		if(shared==null)
		{
			shared = build();
			if(shared.oreBlocks > 0)
			{
				IGLib.IG_LOGGER.info("Generated {} client and {} server resources for {} ore block(s) in {} declared stone type(s)",
						shared.client.size(), shared.server.size(), shared.oreBlocks, shared.stoneTypes);
			}
		}
		return shared;
	}

	public static Assets build()
	{
		Assets assets = new Assets();

		// Tag files accumulate across every declared ore block, so they are gathered before being written out.
		Map<ResourceLocation, Set<String>> blockTags = new TreeMap<>(comparingId());
		Map<String, String> lang = new TreeMap<>();
		Set<String> stoneNames = new LinkedHashSet<>();

		for(DeferredHolder<Block, Block> holder : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(!holder.isBound()) continue;
			Block block = holder.get();
			if(!(block instanceof IOreBlock ore)) continue;
			if(!(ore.getStoneMaterial().instance() instanceof ConfigMaterialStone)) continue;

			ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
			if(blockId==null) continue;

			assets.oreBlocks++;
			stoneNames.add(ore.getStoneMaterial().getName());

			writeBlockState(assets, ore, blockId);
			writeModels(assets, ore, blockId);
			writeLootTable(assets, ore, blockId);
			collectTags(blockTags, ore, blockId);
		}

		if(assets.oreBlocks==0) return assets;

		for(Map.Entry<ResourceLocation, Set<String>> tag : blockTags.entrySet())
		{
			assets.server.put(dataPath(tag.getKey(), "tags/blocks"), tagJson(tag.getValue()));
		}

		for(String stone : stoneNames) lang.put("material."+IGLib.MODID+"."+stone, titleCase(stone));
		assets.client.put(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "lang/en_us.json"), langJson(lang));
		assets.stoneTypes = stoneNames.size();

		return assets;
	}

	private static void writeBlockState(Assets assets, IOreBlock ore, ResourceLocation blockId)
	{
		JsonArray multipart = new JsonArray();

		if(ore instanceof IGWeatheringOreBlock)
		{
			// One part per face per stage
			for(Direction face : Direction.values())
			{
				for(MineralWeathering stage : MineralWeathering.values())
				{
					JsonObject part = new JsonObject();
					part.add("apply", modelVariants(ore, stage, face));
					JsonObject when = new JsonObject();
					when.addProperty(IGWeatheringOreBlock.OXIDATION_PROPERTIES.get(face.get3DDataValue()).getName(),
							stage.getSerializedName());
					part.add("when", when);
					multipart.add(part);
				}
			}
		}
		else
		{
			JsonObject part = new JsonObject();
			part.add("apply", modelVariants(ore, null, null));
			multipart.add(part);
		}

		JsonObject root = new JsonObject();
		root.add("multipart", multipart);
		assets.client.put(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "blockstates/"+blockId.getPath()+".json"), bytes(root));
	}

	private static JsonArray modelVariants(IOreBlock ore, MineralWeathering stage, Direction face)
	{
		JsonArray apply = new JsonArray();
		for(int variation : VARIATIONS)
		{
			JsonObject model = new JsonObject();
			model.addProperty("model", modelId(ore, variation, stage, face).toString());
			apply.add(model);
		}
		return apply;
	}

	private static void writeModels(Assets assets, IOreBlock ore, ResourceLocation blockId)
	{
		if(ore instanceof IGWeatheringOreBlock)
		{
			for(Direction face : Direction.values())
				for(MineralWeathering stage : MineralWeathering.values())
					for(int variation : VARIATIONS)
						putModel(assets, ore, variation, stage, face);
		}
		else
		{
			for(int variation : VARIATIONS) putModel(assets, ore, variation, null, null);
		}

		// The held item always shows the unweathered first variant, as the built-in item models do.
		JsonObject item = modelJson(ore, 1, MineralWeathering.PRISTINE, null);
		assets.client.put(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "models/item/"+blockId.getPath()+".json"), bytes(item));
	}

	private static void putModel(Assets assets, IOreBlock ore, int variation, MineralWeathering stage, Direction face)
	{
		ResourceLocation id = modelId(ore, variation, stage, face);
		assets.client.put(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/"+id.getPath()+".json"),
				bytes(modelJson(ore, variation, stage, face)));
	}

	private static ResourceLocation modelId(IOreBlock ore, int variation, MineralWeathering stage, Direction face)
	{
		String prefix = stoneTypeOf(ore).getRegistryPrefix();
		if(prefix.endsWith("_")) prefix = prefix.substring(0, prefix.length()-1);
		if(prefix.isEmpty()) prefix = "declared";

		StringBuilder path = new StringBuilder("block/ore_block/")
				.append(prefix).append('/')
				.append(ore.getOreRichness().getSanitizedName()).append('/');
		if(stage!=null&&face!=null) path.append(stage.getSerializedName()).append('_');
		path.append(lower(ore.getOreMaterial().getName())).append('_')
				.append(lower(ore.getStoneMaterial().getName()))
				.append("_variation_").append(variation);
		if(face!=null) path.append('_').append(face.getName().toLowerCase(Locale.ROOT));

		return ResourceLocation.fromNamespaceAndPath(IGLib.MODID, path.toString());
	}

	private static JsonObject modelJson(IOreBlock ore, int variation, MineralWeathering stage, Direction face)
	{
		MaterialInterface<?> stone = ore.getStoneMaterial();
		boolean column = stone.useSedimentaryTextures(BlockCategoryFlags.ORE_BLOCK);

		String parent = "block/base/ore_block"+(column?"_sedimentary": "");
		if(face!=null) parent += "/ore_block_"+face.getName().toLowerCase(Locale.ROOT);

		JsonObject textures = new JsonObject();
		ResourceLocation base = stone.getTextureLocation(BlockCategoryFlags.ORE_BLOCK);
		if(column)
		{
			textures.addProperty("side", base.toString()+"_side");
			textures.addProperty("top", base.toString()+"_top");
		}
		else
		{
			textures.addProperty("base", base.toString());
		}
		textures.addProperty(face!=null?"ore_"+face.getName().toLowerCase(Locale.ROOT): "ore",
				oreTexture(ore, variation, stage==null?MineralWeathering.PRISTINE: stage).toString());

		JsonObject model = new JsonObject();
		model.addProperty("parent", ResourceLocation.fromNamespaceAndPath(IGLib.MODID, parent).toString());
		model.add("textures", textures);
		return model;
	}

	/**
	 * The ore overlay is a paletted permutation keyed on the mineral, not the host rock, so a declared rock type
	 * doesn't need new artwork, this setup points at a texture the atlas should already build.
	 */
	private static ResourceLocation oreTexture(IOreBlock ore, int variation, MineralWeathering stage)
	{
		MaterialInterface<?> oreMaterial = ore.getOreMaterial();
		if(oreMaterial.hasCustomTexture(BlockCategoryFlags.ORE_BLOCK))
		{
			return ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "block/colored/"+lower(oreMaterial.getName())
					+"/ore/"+ore.getOreRichness().getSanitizedName()+"_"+variation);
		}

		StoneFormation formation = ((MaterialStone)ore.getStoneMaterial().instance()).getStoneFormation();
		String veinType = formation==StoneFormation.SEDIMENTARY
				?IGVeinTextureType.LAYERED.getSanitizedName()
				: oreMaterial.getVeinTextureType().getSanitizedName();

		return ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "palette/block/ore_bearing/"+veinType+"/"
				+ore.getOreRichness().getSanitizedName()+"_"+variation+"_"
				+stage.getSerializedName()+"_"+lower(oreMaterial.getName()));
	}

	private static void writeLootTable(Assets assets, IOreBlock ore, ResourceLocation blockId)
	{
		String drop = BuiltInRegistries.ITEM.getKey(ore.getItemDrop().getItem())+"";
		int count = ore.getItemDrop().getCount();

		JsonObject silkTouched = itemEntry(drop);
		JsonArray conditions = new JsonArray();
		JsonObject match = new JsonObject();
		match.addProperty("condition", "minecraft:match_tool");
		JsonObject predicate = new JsonObject();
		JsonArray enchantments = new JsonArray();
		JsonObject silk = new JsonObject();
		silk.addProperty("enchantment", "minecraft:silk_touch");
		JsonObject levels = new JsonObject();
		levels.addProperty("min", 1);
		silk.add("levels", levels);
		enchantments.add(silk);
		predicate.add("enchantments", enchantments);
		match.add("predicate", predicate);
		conditions.add(match);
		silkTouched.add("conditions", conditions);

		JsonObject mined = itemEntry(drop);
		JsonArray functions = new JsonArray();
		JsonObject setCount = new JsonObject();
		setCount.addProperty("function", "minecraft:set_count");
		setCount.addProperty("add", false);
		setCount.addProperty("count", (double)count);
		functions.add(setCount);
		JsonObject fortune = new JsonObject();
		fortune.addProperty("function", "minecraft:apply_bonus");
		fortune.addProperty("enchantment", "minecraft:fortune");
		fortune.addProperty("formula", "minecraft:ore_drops");
		functions.add(fortune);
		JsonObject decay = new JsonObject();
		decay.addProperty("function", "minecraft:explosion_decay");
		functions.add(decay);
		mined.add("functions", functions);

		JsonArray children = new JsonArray();
		children.add(silkTouched);
		children.add(mined);
		JsonObject alternatives = new JsonObject();
		alternatives.addProperty("type", "minecraft:alternatives");
		alternatives.add("children", children);

		JsonArray entries = new JsonArray();
		entries.add(alternatives);
		JsonObject pool = new JsonObject();
		pool.addProperty("rolls", 1.0);
		pool.addProperty("bonus_rolls", 0.0);
		pool.add("entries", entries);

		JsonArray pools = new JsonArray();
		pools.add(pool);
		JsonObject root = new JsonObject();
		root.addProperty("type", "minecraft:block");
		root.add("pools", pools);
		root.addProperty("random_sequence", IGLib.MODID+":blocks/"+blockId.getPath());

		assets.server.put(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "loot_tables/blocks/"+blockId.getPath()+".json"), bytes(root));
	}

	private static JsonObject itemEntry(String item)
	{
		JsonObject entry = new JsonObject();
		entry.addProperty("type", "minecraft:item");
		entry.addProperty("name", item);
		return entry;
	}

	private static void collectTags(Map<ResourceLocation, Set<String>> tags, IOreBlock ore, ResourceLocation blockId)
	{
		String id = blockId.toString();
		add(tags, ore.getOreMaterial().getBlockMaterialTag().location(), id);
		add(tags, BlockCategoryFlags.ORE_BLOCK.getCategoryTag().location(), id);
		add(tags, ResourceLocation.fromNamespaceAndPath("c", "ores"), id);
		add(tags, ResourceLocation.fromNamespaceAndPath("minecraft", "mineable/pickaxe"), id);
		add(tags, ResourceLocation.fromNamespaceAndPath("minecraft", "needs_stone_tool"), id);
	}

	private static void add(Map<ResourceLocation, Set<String>> tags, ResourceLocation tag, String value)
	{
		tags.computeIfAbsent(tag, t -> new LinkedHashSet<>()).add(value);
	}

	private static byte[] tagJson(Set<String> values)
	{
		JsonArray array = new JsonArray();
		values.forEach(array::add);
		JsonObject root = new JsonObject();
		root.addProperty("replace", false);
		root.add("values", array);
		return bytes(root);
	}

	private static byte[] langJson(Map<String, String> lang)
	{
		JsonObject root = new JsonObject();
		lang.forEach(root::addProperty);
		return bytes(root);
	}

	private static ResourceLocation dataPath(ResourceLocation tag, String folder)
	{
		return ResourceLocation.fromNamespaceAndPath(tag.getNamespace(), folder+"/"+tag.getPath()+".json");
	}

	private static String titleCase(String raw)
	{
		StringBuilder out = new StringBuilder();
		for(String word : raw.split("_"))
		{
			if(word.isEmpty()) continue;
			if(out.length() > 0) out.append(' ');
			out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
		}
		return out.toString();
	}

	/** The base material of an ore block should always be the host rock itself. */
	private static IStoneType stoneTypeOf(IOreBlock ore)
	{
		return (IStoneType)ore.getStoneMaterial();
	}

	private static String lower(String raw)
	{
		return raw.toLowerCase(Locale.ROOT);
	}

	private static byte[] bytes(JsonObject json)
	{
		return json.toString().getBytes(StandardCharsets.UTF_8);
	}

	private static java.util.Comparator<ResourceLocation> comparingId()
	{
		return java.util.Comparator.comparing(ResourceLocation::toString);
	}
}
