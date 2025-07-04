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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MaterialReinforcedRefractoryBlock extends MaterialMisc
{
	public MaterialReinforcedRefractoryBlock()
	{
		super();
		this.name = "reinforced_refractory_brick";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);
	}

	@Override
	public Properties getProperties(IFlagType<?> flag)
	{
		return IGLib.STONE_DECO_PROPS.sound(SoundType.NETHER_BRICKS)
				.strength(8, 30);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "block/static_block/reinforced_refractory_brick");
	}
}
