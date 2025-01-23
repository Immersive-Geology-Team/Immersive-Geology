/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper;

import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import com.igteam.immersivegeology.common.block.IGScaffoldingBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;

import static com.igteam.immersivegeology.core.registration.IGRegistrationHolder.getBlockRegistryMap;

public class ScaffoldingHelper
{
	private final MaterialHelper scaffolding_material;
	public ScaffoldingHelper(MaterialHelper materialHelper)
	{
		this.scaffolding_material = materialHelper;
	}

	public ScaffoldingHelper(MaterialInterface<?> material)
	{
		this(material.instance());
	}

	public IGScaffoldingBlock getDefault()
	{
		String key = BlockCategoryFlags.SCAFFOLDING.getRegistryKey(scaffolding_material) + "_" + MetalScaffoldingType.STANDARD.name().toLowerCase();
		if(getBlockRegistryMap().containsKey(key)) {
			return (IGScaffoldingBlock) IGRegistrationHolder.getBlock.apply(key);
		}

		IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", key);
		return null;
	}

	public IGScaffoldingBlock getGrate()
	{
		String key = BlockCategoryFlags.SCAFFOLDING.getRegistryKey(scaffolding_material) + "_" + MetalScaffoldingType.GRATE_TOP.name().toLowerCase();
		if(getBlockRegistryMap().containsKey(key)) {
			return (IGScaffoldingBlock) IGRegistrationHolder.getBlock.apply(key);
		}

		IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", key);
		return null;
	}

	public IGScaffoldingBlock getWoodenTop()
	{
		String key = BlockCategoryFlags.SCAFFOLDING.getRegistryKey(scaffolding_material) + "_" + MetalScaffoldingType.WOODEN_TOP.name().toLowerCase();
		if(getBlockRegistryMap().containsKey(key)) {
			return (IGScaffoldingBlock) IGRegistrationHolder.getBlock.apply(key);
		}

		IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", key);
		return null;
	}

	public MaterialHelper getMaterial()
	{
		return scaffolding_material;
	}
}
