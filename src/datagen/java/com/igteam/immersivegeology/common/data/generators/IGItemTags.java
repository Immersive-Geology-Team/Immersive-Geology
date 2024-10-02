/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IETags;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.dries007.tfc.common.TFCTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat.getTFCItemTag;

public class IGItemTags extends ItemTagsProvider
{
	public IGItemTags(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blocks, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, blocks, IGLib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider)
	{
		tag(IETags.toolboxTools).add(IGRegistrationHolder.getItem.apply("ig_toolkit_0"));
		tag(IETags.toolboxTools).add(IGRegistrationHolder.getItem.apply("ig_toolkit_1"));
		for(IFlagType<?> category : IFlagType.getAllRegistryFlags())
		{
			for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
			{
				if(material.hasFlag(category))
				{
					if(category.getValue() instanceof ItemCategoryFlags itemFlag)
					{
						if(material.instance().checkExistingImplementation(ModFlags.MINECRAFT, itemFlag)) continue;
						if(material.instance().checkExistingImplementation(ModFlags.IMMERSIVEENGINEERING, itemFlag)) continue;
						Item item = material.instance().getItem(itemFlag);
						// We use Cookie and Cake in cases where Items do not return a valid value.
						assert item != null;
						if(!(item.equals(Items.COOKIE) || item.equals(Blocks.CAKE.asItem()))) {
							TagKey<Item> item_key = material.getItemTag(itemFlag);
							tag(item_key).add(item);
							if(itemFlag.equals(ItemCategoryFlags.INGOT))
							{
								if(material.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
								if(ModFlags.TFC.isStrictlyLoaded()) {
									TagKey<Item> tagKey = getTFCItemTag("PILEABLE_INGOTS");
									if(tagKey == null)
									{
										IGLib.IG_LOGGER.warn("Skipped TFC Tag due to null tag");
										continue;
									}
									tag(tagKey).add(item);
								}
							}
						}
					}
				}
			}
		}
	}
}
