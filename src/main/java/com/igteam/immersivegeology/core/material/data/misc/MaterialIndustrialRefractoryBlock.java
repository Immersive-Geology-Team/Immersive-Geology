/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.lib.shim.MCShims.SoundType;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import net.minecraft.util.ResourceLocation;

public class MaterialIndustrialRefractoryBlock extends MaterialMisc
{
	public MaterialIndustrialRefractoryBlock()
	{
		super();
		this.name = "industrial_refractory_brick";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);
	}

	@Override
	public IGBlockProperties getProperties(IFlagType<?> flag)
	{
		return IGLib.STONE_DECO_PROPS.sound(SoundType.POLISHED_DEEPSLATE)
				.strength(8, 30);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "block/static_block/industrial_refractory_brick");
	}
}
