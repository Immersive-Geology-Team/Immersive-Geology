/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.common.Tags.Biomes;

import java.util.Optional;

public class MaterialEvaporateMineral extends MaterialMineral
{
	public MaterialEvaporateMineral()
	{
		super();
		addFlags(BlockCategoryFlags.EVAPORATE, BlockCategoryFlags.EVAPORATE_CRYSTAL, ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.SEDIMENT, BlockCategoryFlags.STORAGE_BLOCK);
		removeMaterialFlags(ItemCategoryFlags.SLAG, ItemCategoryFlags.POWDERED_SLAG,
				ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.POOR_ORE,ItemCategoryFlags.RICH_ORE,
				ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.ORE_BLOCK);
		int minSea = 62;
		int maxSea = 72;

		CONFIG = new MineralConfig(8,50,1, minSea, maxSea,5, 0.5,true, Optional.of(Biomes.IS_DESERT), IGGenerationType.EVAPORATE);
	}

	@Override
	public Properties getProperties(IFlagType<?> flag)
	{
		return flag.equals(BlockCategoryFlags.STORAGE_BLOCK) ? IGLib.CRYSTAL_DECO_PROPS : super.getProperties(flag);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		if(flag instanceof BlockCategoryFlags blockFlag)
		{
			switch(blockFlag)
			{
				case EVAPORATE ->
				{
					return new ResourceLocation(IGLib.MODID, "block/colored/"+ name +"/sediment");
				}
			}
		}
		return super.getTextureLocation(flag);
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();

		IGMethodBuilder.mixing(this, IGStageDesignation.PREPARATION).create(
				ItemCategoryFlags.SEDIMENT, FluidTags.WATER, 64, ChemicalEnum.Brine.getCloudySlurryWith(this), 64);

		IGMethodBuilder.mixing(this, IGStageDesignation.PREPARATION).create(
				ItemCategoryFlags.CRYSTAL, FluidTags.WATER, 64, ChemicalEnum.Brine.getSlurryWith(this), 64);

		IGMethodBuilder.centrifuge(this, IGStageDesignation.PURIFICATION).create(
			ChemicalEnum.Brine.getCloudySlurryTagWith(this), 1000,  this,Items.SAND, 1,
				ChemicalEnum.Brine.getSlurryWith(this), 976,
				null, 0, 1200, 614400);
	}
}
