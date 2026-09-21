package com.igteam.immersivegeology.common.block.multiblocks.structure;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// quick n dirty shim for multiblock template conversions.
public final class IGBlockMapping
{
	private record Target(String id, int meta)
	{
	}

	private static final Map<String, Target> MAPPINGS = new HashMap<>();
	private static final Set<String> WARNED = new HashSet<>();

	// will remove later once all NBT Data has been rerecorded for 1.12.2... don't judge me...
	static
	{
		// metal_decoration0 meta order: coil_lv, coil_mv, coil_hv, rs_engineering,
		// light_engineering, heavy_engineering, generator, radiator
		map("immersiveengineering:coil_lv", "immersiveengineering:metal_decoration0", 0);
		map("immersiveengineering:coil_mv", "immersiveengineering:metal_decoration0", 1);
		map("immersiveengineering:coil_hv", "immersiveengineering:metal_decoration0", 2);
		map("immersiveengineering:rs_engineering", "immersiveengineering:metal_decoration0", 3);
		map("immersiveengineering:light_engineering", "immersiveengineering:metal_decoration0", 4);
		map("immersiveengineering:heavy_engineering", "immersiveengineering:metal_decoration0", 5);
		map("immersiveengineering:generator", "immersiveengineering:metal_decoration0", 6);
		map("immersiveengineering:radiator", "immersiveengineering:metal_decoration0", 7);

		// metal_decoration1 meta order: steel_fence, steel_scaffolding_0/1/2,
		// aluminum_fence, aluminum_scaffolding_0/1/2
		map("immersiveengineering:steel_fence", "immersiveengineering:metal_decoration1", 0);
		map("immersiveengineering:steel_scaffolding_standard", "immersiveengineering:metal_decoration1", 1);
		map("immersiveengineering:steel_scaffolding_grate_top", "immersiveengineering:metal_decoration1", 2);
		map("immersiveengineering:steel_scaffolding_wooden_top", "immersiveengineering:metal_decoration1", 3);
		map("immersiveengineering:aluminum_fence", "immersiveengineering:metal_decoration1", 4);
		map("immersiveengineering:aluminum_scaffolding_standard", "immersiveengineering:metal_decoration1", 5);

		// slabs
		map("immersiveengineering:slab_steel_scaffolding_standard", "immersiveengineering:metal_decoration1_slab", 1);
		map("immersiveengineering:slab_steel_scaffolding_grate_top", "immersiveengineering:metal_decoration1_slab", 2);
		map("immersiveengineering:slab_steel_scaffolding_wooden_top", "immersiveengineering:metal_decoration1_slab", 3);

		// sheetmetal is metadata-keyed by metal; steel is index 8 in EnumMetals order
		map("immersiveengineering:sheetmetal_steel", "immersiveengineering:sheetmetal", 8);
		map("immersiveengineering:sheetmetal_iron", "immersiveengineering:sheetmetal", 5);
		map("immersiveengineering:slab_sheetmetal_steel", "immersiveengineering:sheetmetal_slab", 8);

		map("immersiveengineering:storage_steel", "immersiveengineering:storage", 8);

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

	public static IBlockState toState(String blockId)
	{
		String id = stripProperties(blockId);
		if("minecraft:air".equals(id)) return null;

		Target target = MAPPINGS.get(id);
		String resolvedId = target==null?id: target.id();
		int meta = target==null?0: target.meta();

		Block block = Block.REGISTRY.getObject(new ResourceLocation(resolvedId));
		if(block==null||block==Blocks.AIR)
		{
			if(WARNED.add(id)) IGLib.IG_LOGGER.warn("Structure references unmapped block {}", id);
			return null;
		}
		return block.getStateFromMeta(meta);
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
