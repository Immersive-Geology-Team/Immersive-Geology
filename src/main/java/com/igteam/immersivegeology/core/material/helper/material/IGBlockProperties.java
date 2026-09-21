package com.igteam.immersivegeology.core.material.helper.material;

import net.minecraft.block.material.Material;

public class IGBlockProperties
{
	private Material material = Material.ROCK;
	private float hardness = 3.0F;
	private float resistance = 5.0F;

	public static IGBlockProperties of(Object material)
	{
		IGBlockProperties properties = new IGBlockProperties();
		if(material instanceof Material mc) properties.material = mc;
		return properties;
	}

	public static IGBlockProperties copy(Object source)
	{
		return new IGBlockProperties();
	}

	public IGBlockProperties strength(float hardness)
	{
		this.hardness = hardness;
		return this;
	}

	public IGBlockProperties strength(float hardness, float resistance)
	{
		this.hardness = hardness;
		this.resistance = resistance;
		return this;
	}

	public IGBlockProperties sound(Object soundType)
	{
		return this;
	}

	public IGBlockProperties instrument(Object instrument)
	{
		return this;
	}

	public IGBlockProperties mapColor(Object color)
	{
		return this;
	}

	public IGBlockProperties noOcclusion()
	{
		return this;
	}

	public IGBlockProperties requiresCorrectToolForDrops()
	{
		return this;
	}

	public Material getMaterial()
	{
		return material;
	}

	public float getHardness()
	{
		return hardness;
	}

	public float getResistance()
	{
		return resistance;
	}
}
