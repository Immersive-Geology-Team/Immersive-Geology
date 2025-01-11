/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

public interface IGConfigurableMachine
{
	String getName();
	int getDefaultBatchInput();
	int getDefaultBatchOutput();
	int getDefaultTime();
	int getDefaultEnergy();
}
