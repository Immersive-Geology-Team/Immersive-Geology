/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.helper;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;

public enum IGVeinTextureType
{
	METALLIC,
	CRYSTAL,
	LAYERED,
	MINERAL;

	public String getSanitizedName()
	{
		return this.name().toLowerCase();
	}

	public Direction getDirectionalBias(RandomSource random)
	{
		if(this == LAYERED) {
			return random.nextInt(5) == 1 ? Direction.getRandom(random) : Plane.HORIZONTAL.getRandomDirection(random);
		}
		return Direction.getRandom(random);
	}
}

