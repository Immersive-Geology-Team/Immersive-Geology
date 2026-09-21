package com.igteam.immersivegeology.common.block.helper;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum MineralWeathering implements IStringSerializable
{
	PRISTINE,
	TARNISHED,
	CORRODED;

	public MineralWeathering next()
	{
		return this==CORRODED?this: values()[ordinal()+1];
	}

	public String getSanitizedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public String getName()
	{
		return getSanitizedName();
	}
}
