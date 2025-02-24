/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;

public class IGFireStarter extends FlintAndSteelItem implements IGFlagItem
{
	public IGFireStarter()
	{
		super(new Item.Properties().durability(4));
	}

	@Override
	public IFlagType<?> getFlag()
	{
		return ItemCategoryFlags.MISC;
	}

	@Override
	public int getColor(int index)
	{
		return 0xffffff;
	}

	@Override
	public ItemSubGroup getSubGroup()
	{
		return ItemSubGroup.structural;
	}

	@Override
	public Collection<MaterialInterface<?>> getMaterials()
	{
		return List.of();
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture base)
	{
		return null;
	}
}
