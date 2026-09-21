package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

public class IGCreativeTabs
{
	public static final CreativeTabs IG_BASE_TAB = new CreativeTabs(IGLib.MODID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(Items.IRON_PICKAXE);
		}
	};
}
