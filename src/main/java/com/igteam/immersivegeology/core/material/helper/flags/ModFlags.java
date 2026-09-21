package com.igteam.immersivegeology.core.material.helper.flags;

import net.minecraftforge.fml.common.Loader;

import java.util.Locale;

public enum ModFlags implements IFlagType<ModFlags>
{
	MINECRAFT,
	BEYOND_EARTH,
	AD_ASTRA,
	IMMERSIVEENGINEERING,
	TFC;

	@Override
	public ModFlags getValue()
	{
		return this;
	}

	public boolean isLoaded()
	{
		return this==MINECRAFT||Loader.isModLoaded(name().toLowerCase(Locale.ROOT));
	}

	public boolean isStrictlyLoaded()
	{
		return isLoaded();
	}
}
