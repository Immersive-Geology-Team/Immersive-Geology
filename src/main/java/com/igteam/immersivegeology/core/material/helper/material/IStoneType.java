/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A rock type ore spawn in.
 * <p>
 * Everything the ore system needs to know about host rocks goes through here rather now, instead of {@link com.igteam.immersivegeology.core.material.data.enums.StoneEnum}
 * So that a pack developer can extend beyond the built-in enum without every call site caring where a given entry originates.
 * <p>
 * Obtain them from {@link com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes}, inplace of the previous
 * enum iteration.
 */
public interface IStoneType extends MaterialInterface<MaterialStone>
{
	/**
	 * Dense index into per-stone tables, contiguous over the registered stone types and stable for the lifetime of
	 * the game. Used instead of a map lookup on paths that run per block during world generation.
	 */
	int index();

	/** Whether the mod this rock type belongs to is actually loaded. */
	boolean isStoneTypeValid();

	/** Whether this is one of Minecraft's own rock types. */
	boolean isVanilla();

	List<TargetBlockState> getTargets(MineralEnum mineral);

	String getTFCStoneLoc();

	/**
	 * Leading part of the registry name for ore blocks hosted in this rock, including the trailing underscore, or
	 * an empty string for none.
	 * <p>
	 * Part of the block id, so it is frozen for the built-in rock types: changing it would rename blocks in old worlds.
	 * which causes obvious issues.
	 */
	String getRegistryPrefix();

	/** The dimensions that this rock can be found in. */
	Set<ResourceLocation> getDimensions();

	/**
	 * Whether this rock being loaded is enough to conclude it is really in the ground of the dimensions it names.
	 * <p>
	 * True for Minecraft's own stone, and for a rock type someone declared in the configuration.
	 * When declared we take it that it generates there. We have an exception for TerraFirmaCraft's rock,
	 * which is only present when the world itself was built by TFC, so a vanilla world in a TFC pack has none of it;
	 * that case goes through its own hardcoded world-type check instead.
	 */
	boolean declaresPresence();

	/**
	 * The block in the world this rock type is, when that is known up front. Null for the built-in types, which
	 * are still identified by name from the block they are found as.
	 */
	default ResourceLocation getHostBlockId()
	{
		return null;
	}

	static String backdropPaletteKey(MaterialInterface<?> stone)
	{
		if(stone instanceof IStoneType type) return type.getRegistryPrefix()+type.getName().toLowerCase(Locale.ROOT);
		return stone.getName().toLowerCase(Locale.ROOT);
	}

	/** Whether this rock refuses to host the given ore, to allow tuning when other mods have duplicate ores. */
	default boolean excludesOre(MaterialHelper ore)
	{
		return false;
	}
}
