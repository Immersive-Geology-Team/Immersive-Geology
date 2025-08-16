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
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum IGCrystallizerSkins implements IIGMultiSkinHelper
{
	DEFAULT("default", ChatFormatting.GOLD, SkinCreditType.DEVELOPER, "muddykat", false),
	RUSTED("rusted", ChatFormatting.YELLOW, SkinCreditType.DEVELOPER, "muddykat", false);

	private final String skin;
	private final SkinCreditType type;
	private final ChatFormatting formatting;
	private final String credit;
	private final boolean altModel;
	IGCrystallizerSkins(String s, ChatFormatting formatting, SkinCreditType type, String credit, boolean altModel)
	{
		this.skin = s;
		this.formatting = formatting;
		this.type = type;
		this.credit = credit;
		this.altModel = altModel;
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
		return "crystallizer";
	}

	@Override
	public boolean alternativeModel()
	{
		return altModel;
	}

	@Override
	public Enum<?> instance()
	{
		return this;
	}
}
