/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
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

public class MaterialReinforcedRefractoryBlock extends MaterialMisc
{
	public MaterialReinforcedRefractoryBlock()
	{
		super();
		this.name = "reinforced_refractory_brick";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "block/static_block/reinforced_refractory_brick");
	}
}
