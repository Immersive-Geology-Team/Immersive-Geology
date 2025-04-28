/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.skins.helpers;

import net.minecraft.ChatFormatting;

public interface IIGMultiSkinHelper
{
	String getSkin();
	String getCredit();

	String multiblockName();

	ChatFormatting getColor();

	SkinCreditType getType();
	boolean alternativeModel();
}
