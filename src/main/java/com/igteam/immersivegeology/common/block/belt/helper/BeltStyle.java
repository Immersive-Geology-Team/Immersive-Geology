/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.helper;

public record BeltStyle(String material, boolean covered)
{
	public static final BeltStyle STEEL = new BeltStyle("steel", false);
	public static final BeltStyle STEEL_COVERED = new BeltStyle("steel", true);

	public String registryKey(String kind)
	{
		String base = kind.isEmpty()?material+"_belt": material+"_"+kind+"_belt";
		return covered?base+"_covered": base;
	}
}
