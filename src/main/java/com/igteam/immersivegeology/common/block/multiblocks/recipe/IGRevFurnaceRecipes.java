package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class IGRevFurnaceRecipes
{
	private static final int SULFUR_OUTGAS = 25;

	private IGRevFurnaceRecipes()
	{
	}

	public static void register()
	{
		int added = 0;
		added += roast(MineralEnum.Acanthite, 1000);
		added += roast(MineralEnum.Chalcocite, 1000);
		added += roast(MineralEnum.Chalcopyrite, 800);
		added += roast(MineralEnum.Cobaltite, 800);
		added += roast(MineralEnum.Galena, 1000);
		added += roast(MineralEnum.Millerite, 800);
		added += roast(MineralEnum.Molybdenite, 1000);
		added += roast(MineralEnum.Pyrite, 800);
		added += roast(MineralEnum.Sphalerite, 800);

		IGLib.IG_LOGGER.info("- Registered {} reverberation furnace recipes", added);
	}

	private static int roast(MaterialInterface<?> mineral, int time)
	{
		Item input = IGContent.getItem(ItemCategoryFlags.CRUSHED_ORE.getRegistryKey(mineral));
		Item output = IGContent.getItem(ItemCategoryFlags.SLAG.getRegistryKey(mineral));

		if(input==null||output==null)
		{
			IGLib.IG_LOGGER.warn("- roasting skip: {} crushed={} slag={}",
					mineral.getName(), input!=null, output!=null);
			return 0;
		}

		RevFurnaceRecipe.addRecipe(new ItemStack(output, 1), new ItemStack(input, 1), time, SULFUR_OUTGAS);
		return 1;
	}
}
