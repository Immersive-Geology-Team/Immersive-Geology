package com.igteam.immersivegeology.common.util;

import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.Objects;

public class ResourceSorter implements Comparator<ItemStack> {
	@Override
    public int compare(ItemStack o1, ItemStack o2) {

		String namePartOne = "pierwszy", namePartTwo = "drugi";
		try
		{
			namePartOne = Objects.requireNonNull(o1.getItem().getRegistryName()).getPath();
			namePartTwo = Objects.requireNonNull(o2.getItem().getRegistryName()).getPath();
		} catch(NullPointerException e)
		{
			IGLogger.info("An item has no name or is registered wrongly! Forge, how have you even accepted such item?");
		}
    	
        return namePartOne.compareTo(namePartTwo);
    }
}
