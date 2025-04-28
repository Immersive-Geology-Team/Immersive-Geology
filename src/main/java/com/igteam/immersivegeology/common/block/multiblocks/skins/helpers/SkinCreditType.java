/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.skins.helpers;

import net.minecraft.ChatFormatting;

public enum SkinCreditType
{
	DEVELOPER(ChatFormatting.GOLD),
	PETER(ChatFormatting.GRAY),
	ARTIST(ChatFormatting.DARK_GREEN),
	CREATOR(ChatFormatting.DARK_RED),
	SUPPORTER(ChatFormatting.AQUA),
	BUGHUNTER(ChatFormatting.LIGHT_PURPLE),
	MODPACK(ChatFormatting.BLUE);

	private ChatFormatting chatFormat;
	SkinCreditType(ChatFormatting format)
	{
		this.chatFormat = format;
	}


	public ChatFormatting getColor()
	{
		return chatFormat;
	}
}
