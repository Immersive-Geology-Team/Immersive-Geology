package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class IGBlockMapping
{
	private record Target(String id, int meta)
	{
	}

	private static final Map<String, Target> MAPPINGS = new HashMap<>();
	private static final Set<String> WARNED = new HashSet<>();

	static
	{
		map("immersiveengineering:heavy_engineering", "immersiveengineering:metal_decoration0", 2);
		map("immersiveengineering:light_engineering", "immersiveengineering:metal_decoration0", 1);
		map("immersiveengineering:rs_engineering", "immersiveengineering:metal_decoration0", 3);
		map("immersiveengineering:generator", "immersiveengineering:metal_decoration0", 4);
		map("immersiveengineering:radiator", "immersiveengineering:metal_decoration0", 5);
		map("immersiveengineering:coil_lv", "immersiveengineering:metal_decoration0", 6);
		map("immersiveengineering:coil_mv", "immersiveengineering:metal_decoration0", 7);
		map("immersiveengineering:coil_hv", "immersiveengineering:metal_decoration0", 8);

		map("immersiveengineering:steel_scaffolding_standard", "immersiveengineering:metal_decoration1", 4);
		map("immersiveengineering:steel_fence", "immersiveengineering:metal_decoration1", 1);

		map("immersiveengineering:storage_steel", "immersiveengineering:storage", 8);
		map("immersiveengineering:sheetmetal_steel", "immersiveengineering:sheetmetal", 8);
		map("immersiveengineering:sheetmetal_iron", "immersiveengineering:sheetmetal", 5);

		map("immersiveengineering:concrete", "immersiveengineering:stone_decoration", 5);
		map("immersiveengineering:concrete_tile", "immersiveengineering:stone_decoration", 7);
		map("immersiveengineering:blastbrick_reinforced", "immersiveengineering:stone_decoration", 2);

		map("immersiveengineering:fluidpipe", "immersiveengineering:metal_device1", 8);
		map("immersiveengineering:fluid_pipe", "immersiveengineering:metal_device1", 8);

		map("minecraft:air", "minecraft:air", 0);
		map("minecraft:hopper", "minecraft:hopper", 0);
	}

	private IGBlockMapping()
	{
	}

	private static void map(String from, String to, int meta)
	{
		MAPPINGS.put(from, new Target(to, meta));
	}

	public static ItemStack toStack(String blockId)
	{
		String id = stripProperties(blockId);
		if("minecraft:air".equals(id)) return ItemStack.EMPTY;

		Target target = MAPPINGS.get(id);
		String resolvedId = target==null?id: target.id();
		int meta = target==null?0: target.meta();

		Block block = Block.REGISTRY.getObject(new ResourceLocation(resolvedId));
		if(block==null||block==Blocks.AIR)
		{
			if(WARNED.add(id)) IGLib.IG_LOGGER.warn("Structure references unmapped block {}", id);
			return ItemStack.EMPTY;
		}
		return new ItemStack(block, 1, meta);
	}

	private static String stripProperties(String blockId)
	{
		int bracket = blockId.indexOf('[');
		return bracket < 0?blockId: blockId.substring(0, bracket);
	}

	public static boolean isUnmapped(String blockId)
	{
		String id = stripProperties(blockId);
		return !MAPPINGS.containsKey(id)&&Block.REGISTRY.getObject(new ResourceLocation(id))==Blocks.AIR
				&&!"minecraft:air".equals(id);
	}
}
