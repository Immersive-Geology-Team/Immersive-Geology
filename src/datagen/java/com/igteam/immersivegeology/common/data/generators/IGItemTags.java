/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IETags;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
								if(ModFlags.TFC.isStrictlyLoaded())
								{
									TagKey<Item> tagKey = getTFCItemTag("PILEABLE_INGOTS");
									if(tagKey==null)
									{
										IGLib.IG_LOGGER.warn("Skipped TFC Tag due to null tag");
									}
									else
									{
										tag(tagKey).add(item);
									}
								}
								tag(Tags.Items.INGOTS).add(item);
							}
						}
					}

					if(category.getValue() instanceof BlockCategoryFlags blockFlag)
					{
						if(blockFlag.equals(BlockCategoryFlags.ORE_BLOCK))
						{
							generateOreBlockTags(material);
						}
					}
				}
			}
		}
	}

	boolean shouldSkip = false;
	private void generateOreBlockTags(MaterialInterface<?> material)
	{
		for(StoneEnum stone : StoneEnum.values())
		{
			for(OreRichness richness : OreRichness.values())
			{
				IGOreBlock oreBlock = material.getOreBlock(stone, richness);
				if(oreBlock == null) continue;

				Collection<MaterialInterface<?>> materials = oreBlock.getMaterials();
				for(Set<IFlagType<?>> flag_sets : materials.stream().map(MaterialInterface::getFlags).collect(Collectors.toSet()))
				{
					for(IFlagType<?> flag : flag_sets)
					{
						if(flag instanceof ModFlags mod)
						{
							if(!mod.isStrictlyLoaded()) shouldSkip = true;
						}
					}
				}

				if(shouldSkip)
				{
					shouldSkip = false;
					String name = oreBlock.getDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.')+1);
					tag(Tags.Items.ORES).addOptional(new ResourceLocation(IGLib.MODID, id));
					continue;
				}
				tag(Tags.Items.ORES).add(oreBlock.asItem());
			}
		}
	}
}
