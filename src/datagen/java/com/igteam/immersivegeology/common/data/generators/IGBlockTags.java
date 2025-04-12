package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.*;
import com.igteam.immersivegeology.common.block.energypipe.IGEnergyPipe;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
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

		IGLib.IG_LOGGER.info("Started Registration of Immersive Geology Block Tags");
		boolean useOptionalTag = false;
		for(RegistryObject<Block> block : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block.get() instanceof IGFluidBlock fluidBlock)
			{
				//TODO prevent mod only added fluids from being tagged.
				tag(BlockTags.REPLACEABLE).add(fluidBlock);
			}
			if(block.get() instanceof IGFenceBlock fence)
			{
				tag(BlockTags.FENCES).add(fence);
			}
			if(block.get() instanceof IGEvaporateMineralBlock crystal)
			{
				tag(BlockTags.MINEABLE_WITH_SHOVEL).add(crystal);
			}
			if(block.get() instanceof IGCrystalBlock crystal)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(crystal);
			}
			if(block.get() instanceof IGEnergyPipe pipe)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pipe);
			}
			if(block.get() instanceof IOreBlock oreBlock)
			{
				List<MaterialInterface<?>> materials = List.copyOf(oreBlock.getMaterials());

				for(Set<IFlagType<?>> flag_sets : materials.stream().map(MaterialInterface::getFlags).collect(Collectors.toSet()))
				{
					for(IFlagType<?> flag : flag_sets)
					{
						if(flag instanceof ModFlags mod)
						{
							if(!(mod.equals(ModFlags.MINECRAFT) || mod.equals(ModFlags.IMMERSIVEENGINEERING))) useOptionalTag = true;
						}
					}
				}

				if(useOptionalTag) {
					useOptionalTag = false;
					String name = oreBlock.getIGDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.') +1);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(BlockTags.NEEDS_STONE_TOOL).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(Tags.Blocks.ORES).addOptional(new ResourceLocation(IGLib.MODID, id));

					if(ModFlags.TFC.isStrictlyLoaded())
					{
						tag(getTFCBlockTag("CAN_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
						tag(getTFCBlockTag("CAN_START_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
						tag(getTFCBlockTag("CAN_TRIGGER_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
						tag(getTFCBlockTag("POWDERKEG_BREAKING_BLOCKS")).addOptional(new ResourceLocation(IGLib.MODID, id));
						tag(getTFCBlockTag("PROSPECTABLE")).addOptional(new ResourceLocation(IGLib.MODID, id));
					}
					continue;
				}

				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
				tag(BlockTags.NEEDS_STONE_TOOL).add(block.get());
				tag(Tags.Blocks.ORES).add(block.get() );

				if(ModFlags.TFC.isStrictlyLoaded())
				{
					String name = oreBlock.getIGDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.') +1);
					tag(getTFCBlockTag("CAN_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(getTFCBlockTag("CAN_START_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(getTFCBlockTag("CAN_TRIGGER_COLLAPSE")).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(getTFCBlockTag("POWDERKEG_BREAKING_BLOCKS")).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(getTFCBlockTag("PROSPECTABLE")).addOptional(new ResourceLocation(IGLib.MODID, id));
				}
			} else if(block.get() instanceof IGGenericBlock genericBlock)
			{
				List<MaterialInterface<?>> materials = List.copyOf(genericBlock.getMaterials());
				BlockCategoryFlags blockFlag = (BlockCategoryFlags) genericBlock.getFlag();
				if(!genericBlock.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(blockFlag)) {
					 tag(blockFlag.getCategoryTag()).add(genericBlock);
				}

				boolean hasExistingImplementation = false;
				for(MaterialInterface<?> material : materials)
				{
					Set<IFlagType<?>> flag_sets = material.getFlags();
					hasExistingImplementation = material.instance().checkExistingImplementation(genericBlock.getFlag());
					for(IFlagType<?> flag : flag_sets)
					{
						if(flag instanceof ModFlags)
						{
							useOptionalTag = true;
						}
					}
				}

				if(useOptionalTag)
				{
					useOptionalTag = false;
					String name = genericBlock.getIGBlock().getDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.')+1);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(IGLib.MODID, id));
				}
				else
				{
					if(!hasExistingImplementation) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(genericBlock);
				}
			} else if(block.get() instanceof IGSlabBlock slab)
			{
				List<MaterialInterface<?>> materials = List.copyOf(slab.getMaterials());
				boolean hasExistingImplementation = false;
				for(MaterialInterface<?> material : materials)
				{
					Set<IFlagType<?>> flag_sets = material.getFlags();
					hasExistingImplementation = material.instance().checkExistingImplementation(slab.getFlag());
					for(IFlagType<?> flag : flag_sets)
					{
						if(flag instanceof ModFlags)
						{
							useOptionalTag = true;
						}
					}
				}

				if(useOptionalTag)
				{
					useOptionalTag = false;
					String name = slab.getIGBlock().getDescriptionId().toLowerCase();
					String id = name.substring(name.lastIndexOf('.')+1);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(IGLib.MODID, id));
				}
				else
				{
					if(!hasExistingImplementation) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(slab);
				}
			}
		}

		IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);
		this.registerMineable(tag, IGMultiblockProvider.BALLMILL,
				IGMultiblockProvider.BLOOMERY,
				IGMultiblockProvider.CENTRIFUGE,
				IGMultiblockProvider.COREDRILL,
				IGMultiblockProvider.CRYSTALLIZER,
				IGMultiblockProvider.CHEMICAL_REACTOR,
				IGMultiblockProvider.GRAVITY_SEPARATOR,
				IGMultiblockProvider.PELLETIZER,
				IGMultiblockProvider.REVERBERATION_FURNACE,
				IGMultiblockProvider.ROTARYKILN,
				IGMultiblockProvider.TROMMEL);

		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Block Tags");
	}

	private void registerMineable(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag, MultiblockRegistration<?>... entries) {
		MultiblockRegistration[] var3 = entries;
		int var4 = entries.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			MultiblockRegistration<?> entry = var3[var5];
			tag.add((Block)entry.block().get());
		}
	}
}
