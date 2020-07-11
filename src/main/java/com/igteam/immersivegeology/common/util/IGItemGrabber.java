package com.igteam.immersivegeology.common.util;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;

import net.minecraft.item.Item;

public class IGItemGrabber {

	@Nonnull
	public static HashMap<String, Item> IGItemMap = new HashMap<String, Item>();

	public static void inputNewItem(MaterialUseType type, Material mat, Item item) {
		IGItemMap.put(type.getName() + "_" + mat.getName(), item);
	}
	 
	public static void inputNewOreItem(MaterialUseType type, Material baseType, EnumOreBearingMaterials oreType, Item item) {
		String id = type.getName() + "_" + baseType.getName() + "_" + oreType.toString().toLowerCase();
		System.out.println("putting Item in Map with ID: " + id);
		IGItemMap.put(id, item);
	} 
	 
	@Nonnull
	public static Item getIGItem(MaterialUseType type, Material mat) {
		return IGItemMap.get(type.getName() + "_" + mat.getName());
	}

	public static Item getIGItem(MaterialUseType type, PeriodicTableElement gold) {
		// TODO Auto-generated method stub
		return IGItemMap.get(type.getName() + "_" + gold.getName());
	}
	
	@Nonnull
	public static Item getIGOreItem(MaterialUseType type, Material baseType, EnumOreBearingMaterials oreType) {
		return IGItemMap.get(type.getName() + "_" + baseType.getName() + "_" + oreType.toString().toLowerCase());
	}
} 
