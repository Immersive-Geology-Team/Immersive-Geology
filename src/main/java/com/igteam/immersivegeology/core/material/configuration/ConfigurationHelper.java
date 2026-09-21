package com.igteam.immersivegeology.core.material.configuration;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import java.util.ArrayList;
import java.util.List;

public final class ConfigurationHelper
{
	private ConfigurationHelper()
	{
	}

	public static List<ItemCategoryFlags> defaultItemFlags(GeologyMaterial material)
	{
		List<ItemCategoryFlags> list = new ArrayList<ItemCategoryFlags>();

		if(material instanceof MaterialMetal)
		{
			list.add(ItemCategoryFlags.INGOT);
			list.add(ItemCategoryFlags.GEAR);
			list.add(ItemCategoryFlags.PLATE);
			list.add(ItemCategoryFlags.NUGGET);
			list.add(ItemCategoryFlags.CRYSTAL);

			if(material instanceof MaterialNativeMetal)
			{
				list.add(ItemCategoryFlags.NORMAL_ORE);
				list.add(ItemCategoryFlags.POOR_ORE);
				list.add(ItemCategoryFlags.RICH_ORE);
				list.add(ItemCategoryFlags.DIRTY_CRUSHED_ORE);
				list.add(ItemCategoryFlags.CRUSHED_ORE);
			}
		}

		if(material instanceof MaterialMineral)
		{
			list.add(ItemCategoryFlags.NORMAL_ORE);
			list.add(ItemCategoryFlags.POOR_ORE);
			list.add(ItemCategoryFlags.RICH_ORE);
			list.add(ItemCategoryFlags.DIRTY_CRUSHED_ORE);
			list.add(ItemCategoryFlags.CRUSHED_ORE);
			list.add(ItemCategoryFlags.POWDER);
		}

		return list;
	}

	public static List<BlockCategoryFlags> defaultBlockFlags(GeologyMaterial material)
	{
		List<BlockCategoryFlags> list = new ArrayList<BlockCategoryFlags>();

		if(material instanceof MaterialMetal)
		{
			list.add(BlockCategoryFlags.STORAGE_BLOCK);
			list.add(BlockCategoryFlags.SHEETMETAL_BLOCK);
			list.add(BlockCategoryFlags.SHEETMETAL_SLAB);
			list.add(BlockCategoryFlags.SHEETMETAL_STAIRS);
			list.add(BlockCategoryFlags.STAIRS);
			list.add(BlockCategoryFlags.FLUID);

			if(material instanceof MaterialNativeMetal) list.add(BlockCategoryFlags.ORE_BLOCK);
		}

		if(material instanceof MaterialMineral) list.add(BlockCategoryFlags.ORE_BLOCK);

		return list;
	}
}
