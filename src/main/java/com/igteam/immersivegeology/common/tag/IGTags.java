/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.tag;

import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITag;

import java.util.*;

public class IGTags
{
	public static HashMap<IFlagType<?>, HashMap<String, TagKey<Item>>> ITEM_TAG_HOLDER = new HashMap<>();
	public static HashMap<IFlagType<?>, HashMap<String, TagKey<Fluid>>> FLUID_TAG_HOLDER = new HashMap<>();

	private static boolean initialized = false;
	public static synchronized void initialize()
	{
		IGLib.IG_LOGGER.info("======== Initializing Immersive Geology Tags ========");
		for(ItemCategoryFlags itemFlag : ItemCategoryFlags.values())
		{
			ITEM_TAG_HOLDER.put(itemFlag, new HashMap<>());

			for(MaterialInterface<?> materialInterface : IGLib.getGeologyMaterials())
			{
				if(materialInterface.hasFlag(itemFlag))
				{
					if(initialized) IGLib.IG_LOGGER.info("Creating Tag for {} {}", materialInterface.getName(), itemFlag);
					createWrapperForCategory(itemFlag, materialInterface.instance());
				}
			}
		}


		FLUID_TAG_HOLDER.put(BlockCategoryFlags.FLUID, new HashMap<>());
		FLUID_TAG_HOLDER.put(BlockCategoryFlags.SLURRY, new HashMap<>());
		HashMap<String, TagKey<Fluid>> fluid_map = FLUID_TAG_HOLDER.get(BlockCategoryFlags.FLUID);
		HashMap<String, TagKey<Fluid>> slurry_map = FLUID_TAG_HOLDER.get(BlockCategoryFlags.SLURRY);
		for(MaterialInterface<?> materialInterface : IGLib.getGeologyMaterials())
		{
			if(materialInterface.hasFlag(BlockCategoryFlags.FLUID)) {
				String registryKey = BlockCategoryFlags.FLUID.getRegistryKey(materialInterface);
				if(!IGRegistrationHolder.getFluidRegistryMap().containsKey(registryKey)) {
					IGLib.IG_LOGGER.info("Skipping Fluid name {} as it is not in the Fluid Registration Map", registryKey);
					continue;
				}

				MaterialHelper base = materialInterface.instance();
				TagKey<Fluid> tag = FluidTags.create( new ResourceLocation("forge", base.getName().toLowerCase()));

				if(!initialized) IGLib.IG_LOGGER.info("Creating Tag for {} Fluid", materialInterface.getName());
				fluid_map.put(getWrapFromSet(Set.of(base)), tag);
			}

			if(materialInterface.hasFlag(BlockCategoryFlags.SLURRY)) {
				for(MetalEnum metal : MetalEnum.values()){
					if(materialInterface.instance() instanceof MaterialChemical chemical)
					{
						if(!chemical.hasSlurryMetal(metal)) {
							//IGLib.IG_LOGGER.info("Slurry don't exist? {} -> {}", chemical.getName(), metal.getName());
							continue;
						}
						String registryKey = BlockCategoryFlags.SLURRY.getRegistryKey(materialInterface, metal);
						if(!IGRegistrationHolder.getFluidRegistryMap().containsKey(registryKey)) {
							IGLib.IG_LOGGER.info("Skipping Slurry name {} as it is not in the Fluid Registration Map", registryKey);
							continue;
						}

						MaterialHelper base = materialInterface.instance();
						TagKey<Fluid> tag = FluidTags.create( new ResourceLocation("forge", base.getName().toLowerCase() + "_" + metal.getName().toLowerCase()));
						if(!initialized) IGLib.IG_LOGGER.info("Creating Tag for {} {} Slurry", materialInterface.getName(), metal.getName());
						slurry_map.put(getWrapFromSet(Set.of(base, metal.instance())), tag);
					}
				}
			}
		}
		initialized = true;
		IGLib.IG_LOGGER.info("======== Immersive Geology Tags Initialized ========");
	}

	private static void createWrapperForCategory(IFlagType<?> category, GeologyMaterial... materials)
	{
		if(Arrays.stream(materials).anyMatch(m -> m.hasFlag(category)))
		{
			if(category.getValue() instanceof ItemCategoryFlags itemFlag)
			{
				HashMap<String, TagKey<Item>> map = ITEM_TAG_HOLDER.get(itemFlag);
				LinkedHashSet<MaterialHelper> materialSet = new LinkedHashSet<>(Arrays.asList(materials));
				map.put(getWrapFromSet(materialSet), ItemTags.create(wrapCategory(itemFlag, materialSet)));
			}
		}
	}

	private static ResourceLocation wrapCategory(IFlagType<?> category, Set<MaterialHelper> materialSet)
	{
		StringJoiner material_set_name = new StringJoiner("_");
		materialSet.forEach((m -> material_set_name.add(m.getName())));

		return new ResourceLocation("forge", category.getName() + category.getTagPrefix() + "/" + material_set_name);
	}

	public static boolean isInitialized()
	{
		return initialized;
	}

	public static String getWrapFromSet(Set<MaterialHelper> matSet){
		StringJoiner value = new StringJoiner(",");

		for (MaterialHelper m : matSet) {
			value.add(m.getName());
		}
		return "[" + value + "]";
	}
}
