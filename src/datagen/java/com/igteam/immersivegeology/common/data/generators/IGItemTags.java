/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class IGItemTags extends ItemTagsProvider
{
	public IGItemTags(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blocks, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, blocks, IGLib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider)
	{
		for(IFlagType<?> category : IFlagType.getAllRegistryFlags())
		{
			for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
			{
				if(material.hasFlag(category))
				{
					if(category.getValue() instanceof ItemCategoryFlags itemFlag)
					{
						Item item = material.instance().getItem(itemFlag);
						if(item != null) {
							TagKey<Item> item_key = material.getItemTag(itemFlag);
							tag(item_key).add(item);
						}
					}
				}
			}
		}
	}
}
