package com.igteam.immersivegeology.client.menu;

import java.util.Collections;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.handler.CreativeMenuHandler;
import com.igteam.immersivegeology.client.menu.helper.IGSubGroup;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.util.ResourceSorter;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IGItemGroup extends ItemGroup {

	private static ItemSubGroup selectedSubGroup = ItemSubGroup.raw;

	public IGItemGroup(String label) {
		super(label);
	}
	
	@Override
	public ItemStack createIcon() {
		// TODO Add a proper Tab Icon.
		return new ItemStack(Blocks.IRON_ORE);
	}
	
	public static void updateSubGroup(ItemSubGroup group) {
		selectedSubGroup = group;
	}
	
	public static ItemSubGroup getCurrentSubGroup() {
		return selectedSubGroup;
	}
	
	/**
    * Fills {@code items} with all items that are in this group.
    */
	
	@Override
    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> items) {
		for(Item item : Registry.ITEM) {
			if(item instanceof IGSubGroup) {
				IGSubGroup itm = (IGSubGroup)item;
				if(itm.getSubGroup() == selectedSubGroup) {
					item.fillItemGroup(this, items);
				}
			}
		}
		Collections.sort(items, new ResourceSorter());
    }

	public static NonNullList<ItemStack> getCurrentList() {
		NonNullList<ItemStack> list = NonNullList.create();
		for(Item item : Registry.ITEM) {
			if(item instanceof IGSubGroup) {
				IGSubGroup itm = (IGSubGroup)item;
				if(itm.getSubGroup() == selectedSubGroup) {
					item.fillItemGroup(ImmersiveGeology.IGgroup, list);
				}
			}
		}
		return list;
	}
	
}
 