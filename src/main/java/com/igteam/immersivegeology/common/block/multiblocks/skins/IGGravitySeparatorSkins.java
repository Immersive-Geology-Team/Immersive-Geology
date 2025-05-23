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
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum IGGravitySeparatorSkins implements StringRepresentable, IIGMultiSkinHelper
{
	DEFAULT("default", ChatFormatting.GOLD, SkinCreditType.DEVELOPER, "muddykat"),
	GREEN("green", ChatFormatting.DARK_GREEN, SkinCreditType.PETER, "peter"),
	HAZARD("hazard", ChatFormatting.DARK_RED, SkinCreditType.DEVELOPER, "muddykat"),
	STEAMPUNK("steampunk", ChatFormatting.GOLD, SkinCreditType.CREATOR, "wanderingbeekeeper"),
	LEGACY("legacy", ChatFormatting.UNDERLINE, SkinCreditType.DEVELOPER,"muddykat");

	private final String skin;
	private final SkinCreditType type;
	private final ChatFormatting formatting;
	private final String credit;
	IGGravitySeparatorSkins(String s, ChatFormatting formatting, SkinCreditType type, String credit)
	{
		this.skin = s;
		this.formatting = formatting;
		this.type = type;
		this.credit = credit;
	}

	@Override public String getSkin() { return skin; }

	@Override
	public String getCredit()
	{
		return credit;
	}

	@Override public @NotNull String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

	@Override
	public ChatFormatting getColor()
	{
		return formatting;
	}

	@Override
	public String multiblockName()
	{
		return "gravity_separator";
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

	public Item getItem()
	{
		return IGRegistrationHolder.getItem.apply(multiblockName() + "_multiblock_skin_" + getSerializedName());
	}
}
