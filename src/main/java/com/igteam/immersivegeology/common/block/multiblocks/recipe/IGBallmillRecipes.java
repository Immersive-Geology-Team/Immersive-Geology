package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class IGBallmillRecipes
{
	private static final int PROCESS_TIME = 800;
	private static final int PROCESS_ENERGY = 64000;

	private static final ItemCategoryFlags[] INPUTS = {
			ItemCategoryFlags.CRUSHED_ORE,
			ItemCategoryFlags.GRIT
	};

	private IGBallmillRecipes()
	{
	}

	public static void register()
	{
		int added = 0;
		for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
		{
			Item powder = IGContent.getItem(ItemCategoryFlags.POWDER.getRegistryKey(material));
			if(powder==null) continue;

			for(ItemCategoryFlags flag : INPUTS)
			{
				Item source = IGContent.getItem(flag.getRegistryKey(material));
				if(source==null) continue;

				BallmillRecipe.addRecipe(new ItemStack(powder, 1), new ItemStack(source, 1),
						PROCESS_ENERGY, PROCESS_TIME);
				added++;
			}
		}
		IGLib.IG_LOGGER.info("- Registered {} ballmill recipes", added);
	}
}
