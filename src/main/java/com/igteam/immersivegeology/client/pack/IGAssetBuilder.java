package com.igteam.immersivegeology.client.pack;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.ore.IGOreBlock;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IGAssetBuilder
{
	private static final String ROOT = "/assets/"+IGLib.MODID+"/textures/";
	private static final int[] VARIATIONS = {1, 2};

	private IGAssetBuilder()
	{
	}

	public static void build(IGGeneratedPack pack)
	{
		int[] key = readPalette("palette/palette_key.png");
		if(key==null)
		{
			IGLib.IG_LOGGER.error("Missing palette key, generated textures will be skipped");
			return;
		}

		int blocks = 0;
		int items = 0;

		for(Map.Entry<String, Block> entry : blockEntries().entrySet())
			if(entry.getValue() instanceof IGOreBlock ore)
			{
				buildOreBlock(pack, key, entry.getKey(), ore);
				blocks++;
			}

		for(Map.Entry<String, Item> entry : itemEntries().entrySet())
			if(entry.getValue() instanceof IGGenericItem generic)
			{
				buildItem(pack, key, entry.getKey(), generic);
				items++;
			}

		buildLang(pack);

		IGLib.IG_LOGGER.info("- Generated assets for {} ore blocks and {} items ({} resources)",
				blocks, items, pack.size());
	}

	private static void buildLang(IGGeneratedPack pack)
	{
		StringBuilder lang = new StringBuilder();
		lang.append("itemGroup.").append(IGLib.MODID).append("=Immersive Geology\n");

		for(Map.Entry<String, Block> entry : blockEntries().entrySet())
			if(entry.getValue() instanceof IGOreBlock ore)
			{
				String base = "tile."+IGLib.MODID+"."+entry.getKey()+".name";
				lang.append(base).append("=").append(oreDisplayName(ore, OreRichness.NORMAL)).append("\n");

				for(OreRichness richness : OreRichness.values())
				{
					String itemBase = "tile."+IGLib.MODID+"."+entry.getKey()+"."+richness.getSanitizedName();
					lang.append(itemBase).append(".name=").append(oreDisplayName(ore, richness)).append("\n");

					for(MineralWeathering weathering : MineralWeathering.values())
						if(weathering!=MineralWeathering.PRISTINE)
							lang.append(itemBase).append(".").append(weathering.getSanitizedName())
									.append(".name=").append(titleCase(weathering.getSanitizedName())).append(" ")
									.append(oreDisplayName(ore, richness)).append("\n");
				}
			}

		for(Map.Entry<String, Item> entry : itemEntries().entrySet())
			if(entry.getValue() instanceof IGGenericItem generic)
				lang.append("item.").append(IGLib.MODID).append(".").append(entry.getKey()).append(".name=")
						.append(titleCase(generic.getMaterial().getName())).append(" ")
						.append(titleCase(generic.getCategory().getName())).append("\n");

		pack.put(IGLib.MODID+":lang/en_us.lang", lang.toString());
	}

	private static String oreDisplayName(IGOreBlock ore, OreRichness richness)
	{
		return titleCase(richness.getSanitizedName())+" "+titleCase(ore.getOreMaterial().getName())
				+" Ore ("+titleCase(ore.getStoneType().getName())+")";
	}

	private static String titleCase(String raw)
	{
		StringBuilder out = new StringBuilder();
		for(String part : raw.split("_"))
		{
			if(part.isEmpty()) continue;
			if(out.length() > 0) out.append(' ');
			out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
		}
		return out.toString();
	}

	private static Map<String, Block> blockEntries()
	{
		Map<String, Block> map = new HashMap<>();
		for(Block block : IGContent.getRegisteredBlocks())
			if(block.getRegistryName()!=null) map.put(block.getRegistryName().getPath(), block);
		return map;
	}

	private static Map<String, Item> itemEntries()
	{
		Map<String, Item> map = new HashMap<>();
		for(Item item : IGContent.getRegisteredItems())
			if(item.getRegistryName()!=null) map.put(item.getRegistryName().getPath(), item);
		return map;
	}

	private static void buildOreBlock(IGGeneratedPack pack, int[] key, String name, IGOreBlock ore)
	{
		MaterialInterface<?> material = ore.getOreMaterial();
		IStoneType stone = ore.getStoneType();
		String veinType = material.instance().getVeinTextureType().getSanitizedName();
		String stoneTexture = stoneTexture(stone);

		StringBuilder variants = new StringBuilder();
		for(OreRichness richness : OreRichness.values())
			for(MineralWeathering weathering : MineralWeathering.values())
			{
				String modelName = name+"_"+richness.getSanitizedName()+"_"+weathering.getSanitizedName();
				String spriteName = veinSprite(material, veinType, richness, weathering, 1);

				generateVeinSprite(pack, key, material, veinType, richness, weathering, 1);

				pack.put(modelPath("block/"+modelName), oreModel(stoneTexture, spriteName));

				if(variants.length() > 0) variants.append(",\n");
				variants.append("    \"richness=").append(richness.getSanitizedName())
						.append(",weathering=").append(weathering.getSanitizedName())
						.append("\": { \"model\": \"").append(IGLib.MODID).append(":").append(modelName).append("\" }");
			}

		pack.put(blockstatePath(name), "{\n  \"variants\": {\n"+variants+"\n  }\n}\n");

		for(OreRichness richness : OreRichness.values())
			for(MineralWeathering weathering : MineralWeathering.values())
			{
				String variantName = name+"_"+richness.getSanitizedName()+"_"+weathering.getSanitizedName();
				pack.put(modelPath("item/"+variantName),
						"{ \"parent\": \""+IGLib.MODID+":block/"+variantName+"\" }\n");
			}

		String defaultModel = name+"_"+OreRichness.NORMAL.getSanitizedName()+"_"+MineralWeathering.PRISTINE.getSanitizedName();
		pack.put(modelPath("item/"+name),
				"{ \"parent\": \""+IGLib.MODID+":block/"+defaultModel+"\" }\n");
	}

	private static String oreModel(String stoneTexture, String oreSprite)
	{
		return "{\n"
				+"  \"parent\": \"block/block\",\n"
				+"  \"textures\": {\n"
				+"    \"particle\": \""+stoneTexture+"\",\n"
				+"    \"base\": \""+stoneTexture+"\",\n"
				+"    \"ore\": \""+oreSprite+"\"\n"
				+"  },\n"
				+"  \"elements\": [\n"
				+cube("#base", 0, 0, 0, 16, 16, 16, false)+",\n"
				+cube("#ore", -0.02, -0.02, -0.02, 16.02, 16.02, 16.02, true)+"\n"
				+"  ]\n"
				+"}\n";
	}

	private static String cube(String texture, double x1, double y1, double z1, double x2, double y2, double z2,
							   boolean overlay)
	{
		StringBuilder faces = new StringBuilder();
		String[] names = {"north", "east", "south", "west", "up", "down"};
		for(String face : names)
		{
			if(faces.length() > 0) faces.append(",\n");
			faces.append("        \"").append(face).append("\": { \"uv\": [0,0,16,16], \"texture\": \"")
					.append(texture).append("\"");
			if(!overlay) faces.append(", \"cullface\": \"").append(face).append("\"");
			faces.append(" }");
		}
		return "    {\n"
				+"      \"from\": ["+x1+", "+y1+", "+z1+"],\n"
				+"      \"to\": ["+x2+", "+y2+", "+z2+"],\n"
				+"      \"faces\": {\n"+faces+"\n      }\n"
				+"    }";
	}

	private static void buildItem(IGGeneratedPack pack, int[] key, String name, IGGenericItem item)
	{
		MaterialInterface<?> material = item.getMaterial();
		ItemCategoryFlags flag = item.getCategory();

		String texture = paletteItemTexture(pack, key, material, flag);
		if(texture==null) texture = directItemTexture(material, flag);
		if(texture==null) return;

		pack.put(modelPath("item/"+name),
				"{\n  \"parent\": \"item/generated\",\n  \"textures\": { \"layer0\": \""+texture+"\" }\n}\n");
	}

	private static String paletteItemTexture(IGGeneratedPack pack, int[] key, MaterialInterface<?> material,
											 ItemCategoryFlags flag)
	{
		if(!flag.hasPalette()) return null;

		int variation = Math.max(1, material.instance().getPaletteVariation(flag));
		String source = "palette/item/"+flag.getName()+"/type_"+variation;
		if(!exists(source+".png"))
		{
			source = "palette/item/"+flag.getName()+"/type_1";
			if(!exists(source+".png")) return null;
		}

		MineralWeathering weathering = MineralWeathering.PRISTINE;
		String spritePath = "palette/item/"+flag.getName()+"/type_"+variation
				+"_"+weathering.getSanitizedName()+"_"+material.getName();

		if(!pack.has(texturePath(spritePath)))
		{
			byte[] png = tint(key, source, material, weathering);
			if(png==null) return null;
			pack.put(texturePath(spritePath), png);
		}
		return IGLib.MODID+":"+spritePath;
	}

	private static String directItemTexture(MaterialInterface<?> material, ItemCategoryFlags flag)
	{
		String colored = "item/colored/"+material.getName()+"/"+flag.getName();
		if(exists(colored+".png")) return IGLib.MODID+":"+colored;

		String greyscale = greyscaleItemTexture(material, flag);
		if(greyscale!=null&&exists(greyscale+".png")) return IGLib.MODID+":"+greyscale;
		return null;
	}

	private static String greyscaleItemTexture(MaterialInterface<?> material, ItemCategoryFlags flag)
	{
		return switch(flag)
		{
			case INGOT, NUGGET, PLATE, ROD, WIRE, GEAR, COMPOUND_DUST, METAL_OXIDE ->
					"item/greyscale/metal/"+flag.getName();
			case GRIT -> "item/greyscale/grit";
			case POWDER -> "item/greyscale/powder";
			case PELLET -> "item/greyscale/pellet";
			case OXIDE_PELLET -> "item/greyscale/oxide_pellet";
			case SEDIMENT -> "item/greyscale/sediment";
			case MECHANICAL_COMPONENT -> "item/greyscale/mechanical_component";
			case CRUSHED_ORE -> "item/greyscale/rock/crushed_ore";
			case DIRTY_CRUSHED_ORE -> "item/greyscale/rock/dirty_crushed_ore";
			case POWDERED_SLAG -> "item/greyscale/rock/powdered_slag";
			case CLAY -> "item/greyscale/rock/clay";
			case CRYSTAL -> "item/greyscale/crystal/"+material.instance().getCrystalFamily().getName();
			default -> null;
		};
	}

	private static void generateVeinSprite(IGGeneratedPack pack, int[] key, MaterialInterface<?> material,
										   String veinType, OreRichness richness, MineralWeathering weathering,
										   int variation)
	{
		String path = texturePath(veinSpritePath(material, veinType, richness, weathering, variation));
		if(pack.has(path)) return;

		String source = "palette/block/ore_bearing/"+veinType+"/"
				+richness.getSanitizedName()+"_"+variation;
		if(!exists(source+".png")) return;

		byte[] png = tint(key, source, material, weathering);
		if(png!=null) pack.put(path, png);
	}

	private static String veinSprite(MaterialInterface<?> material, String veinType, OreRichness richness,
									 MineralWeathering weathering, int variation)
	{
		return IGLib.MODID+":"+veinSpritePath(material, veinType, richness, weathering, variation);
	}

	private static String veinSpritePath(MaterialInterface<?> material, String veinType, OreRichness richness,
										 MineralWeathering weathering, int variation)
	{
		return "palette/block/ore_bearing/"+veinType+"/"+richness.getSanitizedName()+"_"+variation
				+"_"+weathering.getSanitizedName()+"_"+material.getName();
	}

	private static byte[] tint(int[] key, String sourcePath, MaterialInterface<?> material, MineralWeathering weathering)
	{
		String palettePath = "palette/palettes/"+material.getName()+"/"+weathering.getSanitizedName()+".png";
		int[] target = readPalette(palettePath);
		if(target==null) target = readPalette("palette/palettes/"+material.getName()+"/pristine.png");

		if(target==null)
		{
			if(MISSING_PALETTES.add(material.getName()))
				IGLib.IG_LOGGER.warn("No palette for {}, serving its textures untinted", material.getName());
			return readRaw(sourcePath+".png");
		}

		try(InputStream source = open(sourcePath+".png"))
		{
			if(source==null) return null;
			return IGPalette.applyPalette(source, IGPalette.buildMapping(key, target));
		} catch(IOException exception)
		{
			IGLib.IG_LOGGER.warn("Failed generating {}: {}", sourcePath, exception.getMessage());
			return null;
		}
	}

	private static final java.util.Set<String> MISSING_PALETTES = new java.util.HashSet<>();

	private static byte[] readRaw(String path)
	{
		try(InputStream stream = open(path))
		{
			if(stream==null) return null;
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int read;
			while((read = stream.read(buffer)) > 0) out.write(buffer, 0, read);
			return out.toByteArray();
		} catch(IOException exception)
		{
			return null;
		}
	}

	private static int[] readPalette(String path)
	{
		try(InputStream stream = open(path))
		{
			return stream==null?null: IGPalette.readPaletteRow(stream);
		} catch(IOException exception)
		{
			return null;
		}
	}

	private static InputStream open(String path)
	{
		return IGAssetBuilder.class.getResourceAsStream(ROOT+path);
	}

	private static boolean exists(String path)
	{
		try(InputStream stream = open(path))
		{
			return stream!=null;
		} catch(IOException exception)
		{
			return false;
		}
	}

	private static String stoneTexture(IStoneType stone)
	{
		return "minecraft:blocks/"+stone.instance().getTextureName().toLowerCase(Locale.ROOT);
	}

	private static String blockstatePath(String name)
	{
		return IGLib.MODID+":blockstates/"+name+".json";
	}

	private static String modelPath(String name)
	{
		return IGLib.MODID+":models/"+name+".json";
	}

	private static String texturePath(String name)
	{
		return IGLib.MODID+":textures/"+name+".png";
	}
}
