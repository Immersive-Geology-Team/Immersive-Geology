/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

public enum OrePattern
{
	METALLIC,
	CRYSTAL,
	LAYERED,
	MINERAL;

	public String getName()
	{
		return name().toLowerCase();
	}
}
