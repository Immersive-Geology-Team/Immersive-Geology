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
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Locale;

public enum IGChemicalReactorSkins implements StringRepresentable, IIGMultiSkinHelper
{
	DEFAULT("default", ChatFormatting.GOLD, SkinCreditType.PETER, "peter"),
	LEGACY("legacy", ChatFormatting.UNDERLINE, SkinCreditType.DEVELOPER, "muddykat"),
	RUSTED("rusted", ChatFormatting.YELLOW, SkinCreditType.DEVELOPER, "muddykat"),
	HAZARD("hazard", ChatFormatting.DARK_RED, SkinCreditType.DEVELOPER, "muddykat");

	private final String skin;
	private final SkinCreditType type;
	private final ChatFormatting formatting;
	private final String credit;
	IGChemicalReactorSkins(String s, ChatFormatting formatting, SkinCreditType type, String credit)
	{
		this.skin = s;
		this.formatting = formatting;
		this.type = type;
		this.credit = credit;
	}

	@Override public String getSkin() { return skin; }
	@Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

	@Override
	public String getCredit()
	{
		return credit;
	}

	@Override
	public ChatFormatting getColor()
	{
		return formatting;
	}

	@Override
	public String multiblockName()
	{
		return "chemical_reactor";
	}

	@Override
	public SkinCreditType getType()
	{
		return type;
	}

	@Override
	public boolean alternativeModel()
	{
		return false;
	}
}
