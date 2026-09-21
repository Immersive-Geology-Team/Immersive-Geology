/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.config;

import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Set;

/**
 * A declared rock type, as parsed from the {@link IGStoneTypeConfig}.
 */
public class ConfigStoneEntry
{
	/** Namespaced id, e.g. {@code create:limestone}. Becomes part of every ore block name hosted in this rock. */
	public String id;

	/** The block in the world ore replaces. Defaults to {@link #id}. */
	public String block;

	/** Which minerals will accept this rock, by way of the formations they already accept. */
	public String formation;

	/** Dimensions this rock spawns in. */
	public List<String> dimensions;

	/** Mod that must be loaded for this rock to exist. Defaults to the namespace of {@link #id}. */
	public String requires_mod;

	/** Block texture. Defaults to {@code <namespace>:block/<path>} of {@link #block}. */
	public String texture;

	/** Whether the rock has separate top and side textures rather than one for every face. Null means unset. */
	public Boolean sedimentary_textures;

	/** Block to copy hardness, sound and the rest from. Defaults to {@link #block}. */
	public String properties_from;

	/** Minerals this rock will not host, because something else already provides that ore. */
	public List<String> excluded_minerals;

	/** Filled in during runtime */
	transient ResourceLocation resolvedId;
	transient ResourceLocation resolvedBlock;
	transient ResourceLocation resolvedTexture;
	transient ResourceLocation resolvedProperties;
	transient StoneFormation resolvedFormation;
	transient Set<ResourceLocation> resolvedDimensions;
	transient Set<String> resolvedExclusions;
	transient String resolvedMod;
	transient boolean resolvedColumns;
}
