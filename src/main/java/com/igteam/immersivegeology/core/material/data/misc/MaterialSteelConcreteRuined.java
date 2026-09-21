/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.SoundType;

public class MaterialSteelConcreteRuined extends MaterialMisc
{
	public MaterialSteelConcreteRuined()
	{
		super();
		this.name = "ruined_concrete";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
	}

	public IGBlockProperties getProperties(IFlagType<?> flag)
	{
		return IGBlockProperties.of().sound(SoundType.GILDED_BLACKSTONE).instrument(NoteBlockInstrument.COW_BELL).strength(20, 600);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "block/static_block/concrete_ruined");
	}
}
