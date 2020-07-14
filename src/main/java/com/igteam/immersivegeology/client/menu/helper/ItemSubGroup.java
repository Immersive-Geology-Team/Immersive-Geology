package com.igteam.immersivegeology.client.menu.helper;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum ItemSubGroup
{
	raw(Items.IRON_ORE),
	processed(Items.IRON_INGOT),
	machines(Items.PISTON),
	misc(Items.OAK_SIGN);

	private Item icon;

	ItemSubGroup(Item icon)
	{
		this.icon = icon;
	}

	public Item getIcon()
	{
		return this.icon;
	}
}
