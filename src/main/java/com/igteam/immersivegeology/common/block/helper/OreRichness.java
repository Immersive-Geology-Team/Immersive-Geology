package com.igteam.immersivegeology.common.block.helper;

import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum OreRichness implements IStringSerializable
{
	POOR,
	NORMAL,
	RICH;

	public ItemCategoryFlags toCategory()
	{
		switch(this)
		{
			case POOR:
				return ItemCategoryFlags.POOR_ORE;
			case RICH:
				return ItemCategoryFlags.RICH_ORE;
			default:
				return ItemCategoryFlags.NORMAL_ORE;
		}
	}

	public String getSanitizedName()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public String getName()
	{
		return getSanitizedName();
	}
}
