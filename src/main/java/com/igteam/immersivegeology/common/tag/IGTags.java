/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.tag;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.tags.ITag;

import java.util.HashMap;

public class IGTags
{
	public static HashMap<IFlagType<?>, HashMap<String, TagKey<Item>>> ITEM_TAG_HOLDER = new HashMap<>();

	public void initialize()
	{
		IGLib.IG_LOGGER.info("Initializing Immersive Geology Tags");
		for(ItemCategoryFlags itemFlag : ItemCategoryFlags.values())
		{
			ITEM_TAG_HOLDER.put(itemFlag, new HashMap<>());

			for(MaterialInterface<?> materialInterface : MetalEnum.values())
			{
				if(materialInterface.getFlags().contains(itemFlag)){

				}
			}
		}
	}
}
