/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTags.Blocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class IGBlockTags extends BlockTagsProvider
{
	public IGBlockTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, IGLib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider)
	{
		IGLib.IG_LOGGER.info("IG Block Tags");
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IGOreBlock oreBlock)
			{
				tag(Blocks.PROSPECTABLE).add(oreBlock);
				IGLib.IG_LOGGER.info("\"" + oreBlock.getDescriptionId().toLowerCase() + ".prospected\": \""+oreBlock.getMaterial(MaterialTexture.overlay).getName()+"\",");
				tag(Blocks.CAN_COLLAPSE).add(oreBlock);
				tag(Blocks.CAN_START_COLLAPSE).add(oreBlock);
				tag(Blocks.CAN_TRIGGER_COLLAPSE).add(oreBlock);
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(oreBlock);
				tag(BlockTags.NEEDS_STONE_TOOL).add(oreBlock);
				tag(Blocks.POWDERKEG_BREAKING_BLOCKS).add(oreBlock);
				tag(Tags.Blocks.ORES).add(oreBlock);
			}
		}
	}
}
