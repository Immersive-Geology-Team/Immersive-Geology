/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.special;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.resources.ResourceLocation;

public class MaterialRefractoryBlock extends MaterialMisc
{
	public MaterialRefractoryBlock()
	{
		super();
		this.name = "refractory_brick";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS, ItemCategoryFlags.INGOT);
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
	}

	@Override
	public int getColor(IFlagType<?> p)
	{
		return p == ItemCategoryFlags.INGOT ? 0xD7C8A7 : super.getColor(p);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return flag == ItemCategoryFlags.INGOT ? new ResourceLocation(IGLib.MODID, "item/greyscale/metal/ingot") : new ResourceLocation(IGLib.MODID, "block/static_block/refractory_brick");
	}
}
