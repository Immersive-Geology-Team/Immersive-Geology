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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class MaterialRuined extends MaterialMisc
{
	public MaterialRuined()
	{
		super();
		this.name = "rusty_metal";
		addFlags(BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.MISC, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS,
				BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SHEETMETAL_SLAB, BlockCategoryFlags.SHEETMETAL_STAIRS);
		addFlags(BlockCategoryFlags.SCAFFOLDING, ItemCategoryFlags.PLATE, BlockCategoryFlags.ENGINEERING_BLOCK,
				BlockCategoryFlags.ADVANCED_ENGINEERING_BLOCK, BlockCategoryFlags.MISC);

		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		if(flag instanceof BlockCategoryFlags blockFlag)
		{
			return switch(blockFlag)
			{
				case STORAGE_BLOCK, SLAB, STAIRS -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/block");
				case ENGINEERING_BLOCK -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/light");
				case ADVANCED_ENGINEERING_BLOCK -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/heavy");
				case MISC -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/deco");
				case SCAFFOLDING -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/scaffolding/scaffolding");
				case SHEETMETAL_BLOCK, SHEETMETAL_SLAB, SHEETMETAL_STAIRS -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/sheetmetal");
				default -> super.getTextureLocation(flag);
			};
		}
		if(flag instanceof ItemCategoryFlags itemFlag)
		{
			return switch(itemFlag)
			{
				case PLATE -> new ResourceLocation(IGLib.MODID, "item/colored/rusty_metal/scrap");
				case GRIT -> new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/scaffolding/scaffolding");
				default -> super.getTextureLocation(flag);
			};
		}
		return new ResourceLocation(IGLib.MODID, "block/colored/rusty_metal/deco");
	}
}
