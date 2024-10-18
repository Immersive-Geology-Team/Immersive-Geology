package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.IGFluidBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
		boolean shouldSkip = false;
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IGFluidBlock fluidBlock)
			{
				//TODO prevent mod only added fluids from being tagged.
				tag(BlockTags.REPLACEABLE).add(fluidBlock);
			}
			if(block.get() instanceof IGOreBlock oreBlock)
			{
				List<MaterialInterface<?>> materials = List.copyOf(oreBlock.getMaterials());

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

				if(shouldSkip) {
					shouldSkip = false;
					String name = oreBlock.getBlock().getDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.') +1);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(BlockTags.NEEDS_STONE_TOOL).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(Tags.Blocks.ORES).addOptional(new ResourceLocation(IGLib.MODID, id));
					continue;
				}

				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(oreBlock);
				tag(BlockTags.NEEDS_STONE_TOOL).add(oreBlock);
				tag(Tags.Blocks.ORES).add(oreBlock);

				if(ModFlags.TFC.isStrictlyLoaded())
				{
					String name = oreBlock.getBlock().getDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.') +1);
					tag(getTFCBlockTag("CAN_COLLAPSE")).addOptional(new ResourceLocation("tfc", id));
					tag(getTFCBlockTag("CAN_START_COLLAPSE")).addOptional(new ResourceLocation("tfc", id));
					tag(getTFCBlockTag("CAN_TRIGGER_COLLAPSE")).addOptional(new ResourceLocation("tfc", id));
					tag(getTFCBlockTag("POWDERKEG_BREAKING_BLOCKS")).addOptional(new ResourceLocation("tfc", id));
					tag(getTFCBlockTag("PROSPECTABLE")).addOptional(new ResourceLocation("tfc", id));
				}
			}
		}
	}
}
