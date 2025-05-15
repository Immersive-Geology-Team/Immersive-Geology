/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.skins.helpers;

import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;

import java.util.Locale;

public interface IIGMultiSkinHelper
{
	String getSkin();
	String getCredit();
	String name();
	String multiblockName();

	ChatFormatting getColor();

	SkinCreditType getType();
	boolean alternativeModel();

	default Item getItem()
	{
		return IGRegistrationHolder.getItem.apply(multiblockName() + "_multiblock_skin_" + name().toLowerCase(Locale.ROOT));
	}
}
