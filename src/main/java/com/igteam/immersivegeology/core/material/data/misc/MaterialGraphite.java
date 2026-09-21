/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.util.ResourceLocation;

public class MaterialGraphite extends MaterialMisc
{
	public MaterialGraphite()
	{
		super();
		this.name = "graphite";
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
		addFlags(ItemCategoryFlags.MOLD_BLANK, ItemCategoryFlags.MOLD_PLATE, ItemCategoryFlags.MOLD_GEAR,
				ItemCategoryFlags.MOLD_ROD, ItemCategoryFlags.MOLD_WIRE, ItemCategoryFlags.MOLD_BLOCK,
				ItemCategoryFlags.MOLD_INGOT, ItemCategoryFlags.MOLD_NUGGET);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "item/colored/"+getName()+"/"+flag.getName());
	}
}
