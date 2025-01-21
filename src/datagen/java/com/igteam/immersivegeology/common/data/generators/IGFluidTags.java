/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.CompletableFuture;

import static com.igteam.immersivegeology.common.data.helper.TFCDatagenCompat.getTFCFluidTag;

public class IGFluidTags extends FluidTagsProvider
{
	public IGFluidTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, IGLib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider)
	{
		IGLib.IG_LOGGER.info("Starting Registration of Immersive Geology Fluid Tags");
		for(RegistryObject<Fluid> holder : IGRegistrationHolder.getFluidRegistryMap().values())
		{
			if(holder.get() instanceof IGFluid fluid)
			{
				// Skip Flowing State Fluid
				if(!fluid.getSource().equals(fluid)) continue;
				if(fluid.getMaterial(MaterialTexture.overlay) != null)
				{
					if(fluid.getFlag() instanceof BlockCategoryFlags fluid_flag)
					{
						TagKey<Fluid> fluid_key = fluid.getMaterial(MaterialTexture.base).getFluidTag(fluid_flag, fluid.getMaterial(MaterialTexture.overlay));
						if(fluid_key!=null)
						{
							IGLib.IG_LOGGER.info("Tag for Fluid {} of type {}", fluid_key, fluid_flag.getName());
							tag(fluid_key).add(fluid.getSource());
						}
					}
					continue;
				}

				TagKey<Fluid> fluid_key = fluid.getMaterial(MaterialTexture.base).getFluidTag(BlockCategoryFlags.FLUID);
				if(fluid_key != null) {
					tag(fluid_key).add(fluid.getSource());
					MaterialInterface<?> base = fluid.getMaterial(MaterialTexture.base);
					if(base.hasFlag(MaterialFlags.EXISTING_IMPLEMENTATION)) continue;
					if(base instanceof MetalEnum metal)
					{
						if(ModFlags.TFC.isStrictlyLoaded())
						{
							try {
								//tag(getTFCFluidTag("LAVA_LIKE")).add(fluid);
								tag(getTFCFluidTag("USABLE_IN_INGOT_MOLD")).add(fluid);
							} catch(NullPointerException exception)
							{
								IGLib.IG_LOGGER.error("Cannot Load TFC Fluid Tag: {}", exception.getMessage());
							}
						}
					}
				}
			}
		}
		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Fluid Tags");
	}
}
