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
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MaterialRefractoryBlock extends MaterialMisc
{
	public MaterialRefractoryBlock()
	{
		super();
		this.name = "refractory_brick";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
		this.colorFunction = (f,i) -> 0xffffffff;
	}

	@Override
	public Properties getProperties(IFlagType<?> flag)
	{
		return IGLib.STONE_DECO_PROPS;
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return flag == ItemCategoryFlags.INGOT ? new ResourceLocation(IGLib.MODID, "item/greyscale/metal/ingot") : new ResourceLocation(IGLib.MODID, "block/static_block/refractory_brick");
	}
}
