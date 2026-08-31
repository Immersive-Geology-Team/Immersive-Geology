package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.*;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipe;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntityType;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.ore.IGCrystalBlock;
import com.igteam.immersivegeology.common.block.ore.IGEvaporateMineralBlock;
import com.igteam.immersivegeology.common.block.structural.IGFenceBlock;
import com.igteam.immersivegeology.common.block.structural.IGScaffoldingBlock;
import com.igteam.immersivegeology.common.block.structural.IGSlabBlock;
import com.igteam.immersivegeology.common.block.structural.IGStairBlock;
import com.igteam.immersivegeology.common.fluid.IGFluidBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat.getTFCBlockTag;
import static com.igteam.immersivegeology.core.registration.IGMultiblockProvider.ALL_IG_MULTIBLOCKS;

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
				if(!fence.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(BlockCategoryFlags.FENCE))
				{
					tag(BlockTags.FENCES).add(fence);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).add(fence);
				}
			}
			if(block.get() instanceof IGEvaporateMineralBlock crystal)
			{
				tag(BlockTags.MINEABLE_WITH_SHOVEL).add(crystal);
			}
			if(block.get() instanceof IGCrateEntityType crate)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(crate);
			}
			if(block.get() instanceof IGCrystalBlock crystal)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(crystal);
			}
			if(block.get() instanceof IGStairBlock stair)
			{
				if(!stair.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(stair.getFlag())) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(stair);
			}
			if(block.get() instanceof IGSlabBlock slab)
			{
				if(!slab.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(slab.getFlag())) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(slab);
			}
			if(block.get() instanceof IGEnergyPipe pipe)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pipe);
			}
			if(block.get() instanceof IGScaffoldingBlock scaffoldingBlock)
			{
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(scaffoldingBlock);
			}
			if(block.get() instanceof IOreBlock oreBlock)
			{
				Block ore = block.get();
				if(!oreBlock.getStoneMaterial().hasFlag(ModFlags.MINECRAFT) || !oreBlock.getStoneMaterial().hasFlag(ModFlags.IMMERSIVEENGINEERING)) useOptionalTag = true;

				TagKey<Block> ore_material_tag = oreBlock.getOreMaterial().getBlockMaterialTag();
				TagKey<Block> ore_block = BlockCategoryFlags.ORE_BLOCK.getCategoryTag();

				if(useOptionalTag) {
					useOptionalTag = false;
					String name = oreBlock.getIGDescriptionId().toLowerCase(Locale.ROOT);
					String id = name.substring(name.lastIndexOf('.') +1);
					tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(BlockTags.NEEDS_STONE_TOOL).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(Tags.Blocks.ORES).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(ore_material_tag).addOptional(new ResourceLocation(IGLib.MODID, id));
					tag(ore_block).addOptional(new ResourceLocation(IGLib.MODID, id));
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

				tag(ore_material_tag).add(ore);
				tag(ore_block).add(ore);
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore);
				tag(BlockTags.NEEDS_STONE_TOOL).add(ore);
				tag(Tags.Blocks.ORES).add(ore);

				if(ModFlags.TFC.isStrictlyLoaded())
				{
					String name = oreBlock.getIGDescriptionId().toLowerCase(Locale.ROOT);
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
					String name = genericBlock.getIGBlock().getDescriptionId().toLowerCase(Locale.ROOT);
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
					String name = slab.getIGBlock().getDescriptionId().toLowerCase(Locale.ROOT);
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
		for(MultiblockRegistration<?> multi : ALL_IG_MULTIBLOCKS) tag.add(multi.block().get());


		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Block Tags");
	}

}
