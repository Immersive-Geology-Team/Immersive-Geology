/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.skins;

import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.SkinCreditType;
import net.minecraft.ChatFormatting;

import java.util.Locale;

public enum IGCoreDrillSkins implements IIGMultiSkinHelper
{
	DEFAULT("default", ChatFormatting.GOLD, SkinCreditType.PETER, "peter"),
	SEAFOAM("seafoam", ChatFormatting.LIGHT_PURPLE, SkinCreditType.SUPPORTER, "radioisotopics");

	private final String skin;
	private final SkinCreditType type;
	private final ChatFormatting formatting;
	private final String credit;
	IGCoreDrillSkins(String s, ChatFormatting formatting, SkinCreditType type, String credit)
	{
		this.skin = s;
		this.formatting = formatting;
		this.type = type;
		this.credit = credit;
	}

	@Override public String getSkin() { return skin; }

	@Override
	public SkinCreditType getType()
	{
		return type;
	}

	@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

	@Override
	public ChatFormatting getColor()
	{
		return formatting;
	}

	@Override
	public String getCredit()
	{
		return credit;
	}

	@Override
	public String multiblockName()
	{
		return "coredrill";
	}

	@Override
	public boolean alternativeModel()
	{
		return false;
	}

	@Override
	public Enum<?> instance()
	{
		return this;
	}
}
