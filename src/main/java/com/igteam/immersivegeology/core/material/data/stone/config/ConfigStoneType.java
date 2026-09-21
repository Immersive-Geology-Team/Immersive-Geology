/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.config;

import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A rock type someone declared in the configuration file.
 * <p>
 * Sits alongside {@link com.igteam.immersivegeology.core.material.data.enums.StoneEnum} in
 * {@link com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes}; nothing downstream distinguishes
 * the two beyond what this interface exposes.
 */
public class ConfigStoneType implements IStoneType
{
	private final ConfigStoneEntry entry;
	private final ConfigMaterialStone material;
	private final int index;
	private final String registryPrefix;

	public ConfigStoneType(ConfigStoneEntry entry, int index)
	{
		this.entry = entry;
		this.index = index;
		this.material = new ConfigMaterialStone(entry);
		// Namespace rather than a ModFlags constant, which is a closed enum of five and could never name an
		// arbitrary mod. Sanitised because this ends up in a block registry name.
		this.registryPrefix = sanitise(entry.resolvedId.getNamespace())+"_";
	}

	private static String sanitise(String raw)
	{
		return raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
	}

	@Override
	public MaterialStone instance()
	{
		return material;
	}

	@Override
	public int index()
	{
		return index;
	}

	@Override
	public boolean isStoneTypeValid()
	{
		return ModList.get().isLoaded(entry.resolvedMod);
	}

	@Override
	public boolean isVanilla()
	{
		return "minecraft".equals(entry.resolvedId.getNamespace());
	}

	@Override
	public List<TargetBlockState> getTargets(MineralEnum mineral)
	{
		return List.of();
	}

	@Override
	public String getTFCStoneLoc()
	{
		return entry.resolvedBlock.toString();
	}

	@Override
	public String getRegistryPrefix()
	{
		return registryPrefix;
	}

	@Override
	public Set<ResourceLocation> getDimensions()
	{
		return entry.resolvedDimensions;
	}

	/** Declaring a rock type is a statement that it is found in the dimensions this config entry names. */
	@Override
	public boolean declaresPresence()
	{
		return true;
	}

	@Override
	public ResourceLocation getHostBlockId()
	{
		return entry.resolvedBlock;
	}

	@Override
	public boolean excludesOre(MaterialHelper ore)
	{
		return entry.resolvedExclusions.contains(ore.getName().toLowerCase(Locale.ROOT));
	}

	public ConfigStoneEntry getEntry()
	{
		return entry;
	}

	@Override
	public String toString()
	{
		return "ConfigStoneType["+entry.resolvedId+"]";
	}
}
