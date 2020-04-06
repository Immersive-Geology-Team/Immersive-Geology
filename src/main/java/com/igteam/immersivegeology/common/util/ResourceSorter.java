package com.igteam.immersivegeology.common.util;

import java.util.Comparator;

import net.minecraft.item.ItemStack;

public class ResourceSorter implements Comparator<ItemStack> {
    @Override
    public int compare(ItemStack o1, ItemStack o2) {
    	
    	String namePartOne = o1.getItem().getDisplayName(o1).getString();
    	
    	String onehalf = namePartOne.substring(namePartOne.indexOf(" "));
    	String otherOneHalf = namePartOne.substring(0,namePartOne.indexOf(" "));
    	
    	namePartOne = onehalf + otherOneHalf;

    	String namePartTwo = o2.getItem().getDisplayName(o2).getString();
    	String twohalf = namePartTwo.substring(namePartTwo.indexOf(" "));
    	String otherTwoHalf = namePartTwo.substring(0,namePartTwo.indexOf(" "));
    	
    	namePartTwo = twohalf + otherTwoHalf;
    	
    	System.out.println(namePartOne);
    	
        return namePartOne.compareTo(namePartTwo);
    }
}
