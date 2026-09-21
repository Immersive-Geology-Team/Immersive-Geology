/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.config;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.function.BiFunction;

/**
 * The rock behind a configuration-declared stone type.
 * <p>
 * A built-in rock type is a handwritten class, mostly due to legacy now, but that system allows far more custom behavior for indivudual rock types.
 * This one has only what the configuration entry gave it, which is why there is a single class rather than one per declared rock.
 */
public class ConfigMaterialStone extends MaterialStone
{
	private final ConfigStoneEntry entry;

	public ConfigMaterialStone(ConfigStoneEntry entry)
	{
		super();
		this.entry = entry;
		this.name = entry.resolvedId.getPath();
		this.unserialized_name = entry.resolvedId.getPath();
		this.STONE_FORMATION = entry.resolvedFormation;
		this.DIMENSIONS = entry.resolvedDimensions;
		// The rock itself belongs to another mod, so Immersive Geology never registers a block for it - only ore
		// blocks hosted in it.
		addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
	}

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
	{
		// White leaves the host rock's own texture untinted.
		return ((p, i) -> (p==BlockCategoryFlags.ORE_BLOCK?0xffffff: 0x888c8d));
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return entry.resolvedTexture;
	}

	@Override
	public boolean useColumnBlockStyle(IFlagType<?> flag)
	{
		return entry.resolvedColumns;
	}

	@Override
	public IGBlockProperties getProperties(IFlagType<?> flag)
	{
		Block source = ForgeRegistries.BLOCKS.getValue(entry.resolvedProperties);
		// Null when the declaring mod is absent or renamed the block. Ore in this rock will not generate either
		// way, so plain stone is a mostly harmless stand-in and keeps registration from failing.
		return IGBlockProperties.copy(source!=null?source: Blocks.STONE);
	}

	public ConfigStoneEntry getEntry()
	{
		return entry;
	}
}
