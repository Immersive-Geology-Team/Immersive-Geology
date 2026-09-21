package com.igteam.immersivegeology.client.helper;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;

import java.util.Locale;
import java.util.Random;

public enum IGVeinTextureType
{
	METALLIC,
	CRYSTAL,
	LAYERED,
	MINERAL,
	NATIVE_METAL;

	public boolean hasStoneBackdrop()
	{
		return this==NATIVE_METAL;
	}

	public String getSanitizedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	public EnumFacing getDirectionalBias(Random random)
	{
		if(this==LAYERED&&random.nextInt(5)!=1)
			return Plane.HORIZONTAL.random(random);
		return EnumFacing.random(random);
	}
}
