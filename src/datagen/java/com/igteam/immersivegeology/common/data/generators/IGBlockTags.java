/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.IGFluidBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat.getTFCBlockTag;

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
			if(block.get() instanceof IGFluidBlock fluidBlock)
			{
				tag(BlockTags.REPLACEABLE).add(fluidBlock);
			}
			if(block.get() instanceof IGOreBlock oreBlock)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(oreBlock);
				tag(BlockTags.NEEDS_STONE_TOOL).add(oreBlock);
				tag(Tags.Blocks.ORES).add(oreBlock);

				if(ModFlags.TFC.isStrictlyLoaded())
				{
					tag(getTFCBlockTag("CAN_COLLAPSE")).add(oreBlock);
					tag(getTFCBlockTag("CAN_START_COLLAPSE")).add(oreBlock);
					tag(getTFCBlockTag("CAN_TRIGGER_COLLAPSE")).add(oreBlock);
					tag(getTFCBlockTag("POWDERKEG_BREAKING_BLOCKS")).add(oreBlock);
					tag(getTFCBlockTag("PROSPECTABLE")).add(oreBlock);
				}
			}
		}
	}
}
