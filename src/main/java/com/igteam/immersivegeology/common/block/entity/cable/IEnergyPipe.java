/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.cable;


interface IEnergyPipe {
	default int getTransferableAmount(IGEnergyPipe pipe) {
		return hasVoltageLimit() ? getVoltageLimit(pipe) : Integer.MAX_VALUE;
	}

	default int getVoltageLimit(IGEnergyPipe pipe)
	{
		return 100;
	}

	default boolean hasVoltageLimit()
	{
		return false;
	}
}
