package com.igteam.immersivegeology.common.util;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;

import net.minecraft.item.Item;

public class IGItemGrabber {

	@Nonnull
	public static HashMap<String, Item> IGItemMap = new HashMap<String, Item>();

	public static void inputNewItem(MaterialUseType type, Material mat, Item item) {
		IGItemMap.put(type.getName() + "_" + mat.getName(), item);
	}
	
	@Nonnull
	public static Item getIGItem(MaterialUseType type, Material mat) {
		return IGItemMap.get(type.getName() + "_" + mat.getName());
	}

	public static Item getIGItem(MaterialUseType type, PeriodicTableElement gold) {
		// TODO Auto-generated method stub
		return IGItemMap.get(type.getName() + "_" + gold.getName());
	}
}
