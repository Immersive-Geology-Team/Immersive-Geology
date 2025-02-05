/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.manual.helper;

import blusunrize.immersiveengineering.ImmersiveEngineering;

public enum IGManualType
{
	item_display(ImmersiveEngineering.MODID),
	crafting(ImmersiveEngineering.MODID),
	table(ImmersiveEngineering.MODID);

	private final String modid;

	private IGManualType(String modid){
		this.modid = modid;
	}

	public String get() {
		return name();
	}
}
