/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.tag;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class IGTags
{
	public static LinkedHashMap<IFlagType<?>, LinkedHashMap<String, TagKey<Item>>> ITEM_TAG_HOLDER = new LinkedHashMap<>();
	public static LinkedHashMap<IFlagType<?>, LinkedHashMap<String, TagKey<Fluid>>> FLUID_TAG_HOLDER = new LinkedHashMap<>();

	private static boolean initialized = false;
	public static synchronized void initialize()
	{
		IGLib.IG_LOGGER.info("======== Initializing Immersive Geology Tags ========");
		for(ItemCategoryFlags itemFlag : ItemCategoryFlags.values())
		{
			ITEM_TAG_HOLDER.put(itemFlag, new LinkedHashMap<>());

			for(MaterialInterface<?> materialInterface : IGLib.getGeologyMaterials())
			{
				if(materialInterface.hasFlag(itemFlag))
				{
					createWrapperForCategory(itemFlag, materialInterface.instance());
				}
			}
		}


		FLUID_TAG_HOLDER.put(BlockCategoryFlags.FLUID, new LinkedHashMap<>());
		FLUID_TAG_HOLDER.put(BlockCategoryFlags.SLURRY, new LinkedHashMap<>());
		FLUID_TAG_HOLDER.put(BlockCategoryFlags.CLOUDY_SLURRY, new LinkedHashMap<>());
		HashMap<String, TagKey<Fluid>> fluid_map = FLUID_TAG_HOLDER.get(BlockCategoryFlags.FLUID);
		HashMap<String, TagKey<Fluid>> slurry_map = FLUID_TAG_HOLDER.get(BlockCategoryFlags.SLURRY);
		HashMap<String, TagKey<Fluid>> cloud_slurry_map = FLUID_TAG_HOLDER.get(BlockCategoryFlags.CLOUDY_SLURRY);

		LinkedHashSet<MaterialInterface<?>> slurry_material_set = new LinkedHashSet<>(List.of(MetalEnum.values()));
		slurry_material_set.addAll(List.of(MineralEnum.values()));

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

				LinkedHashSet<MaterialHelper> base_set = new LinkedHashSet<>();
				base_set.add(base);
				fluid_map.put(getWrapFromSet(base_set), tag);
			}


			if(materialInterface.hasFlag(BlockCategoryFlags.SLURRY)) {
				for(MaterialInterface<?> slurry_material : slurry_material_set){
					if(materialInterface.instance() instanceof MaterialChemical chemical)
					{
						if(!chemical.hasSlurryWith(slurry_material)) {
							continue;
						}
						String registryKey = BlockCategoryFlags.SLURRY.getRegistryKey(materialInterface, slurry_material);
						if(!IGRegistrationHolder.getFluidRegistryMap().containsKey(registryKey)) {
							IGLib.IG_LOGGER.info("Skipping Slurry name {} as it is not in the Fluid Registration Map", registryKey);
							continue;
						}

						MaterialHelper base = materialInterface.instance();
						TagKey<Fluid> tag = FluidTags.create( new ResourceLocation("forge", "clean_"+base.getName().toLowerCase() + "_" + slurry_material.getName().toLowerCase()));
						LinkedHashSet<MaterialHelper> base_set = new LinkedHashSet<>();
						base_set.add(base);
						base_set.add(slurry_material.instance());
						slurry_map.put(getWrapFromSet(BlockCategoryFlags.SLURRY, base_set), tag);
					}
				}
			}

			if(materialInterface.hasFlag(BlockCategoryFlags.CLOUDY_SLURRY)) {
				for(MaterialInterface<?> slurry_material : MineralEnum.values()){
					if(materialInterface.instance() instanceof MaterialChemical chemical)
					{
						if(!chemical.hasSlurryWith(slurry_material)) {
							continue;
						}
						String registryKey = BlockCategoryFlags.CLOUDY_SLURRY.getRegistryKey(materialInterface, slurry_material);
						if(!IGRegistrationHolder.getFluidRegistryMap().containsKey(registryKey)) {
							IGLib.IG_LOGGER.info("Skipping CLOUDY_SLURRY name {} as it is not in the Fluid Registration Map", registryKey);
							continue;
						}

						MaterialHelper base = materialInterface.instance();
						TagKey<Fluid> tag = FluidTags.create( new ResourceLocation("forge", "cloudy_"+base.getName().toLowerCase() + "_" + slurry_material.getName().toLowerCase()));
						LinkedHashSet<MaterialHelper> base_set = new LinkedHashSet<>();
						base_set.add(base);
						base_set.add(slurry_material.instance());
						cloud_slurry_map.put(getWrapFromSet(BlockCategoryFlags.CLOUDY_SLURRY, base_set), tag);
					}
				}
			}
		}
		initialized = true;
		IGLib.IG_LOGGER.info("Finished");
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

	private static ResourceLocation wrapCategory(IFlagType<?> category, HashSet<MaterialHelper> materialSet)
	{
		StringJoiner material_set_name = new StringJoiner("_");
		materialSet.forEach((m -> material_set_name.add(m.getName())));

		return new ResourceLocation("forge", category.getName() + category.getTagPrefix() + "/" + material_set_name);
	}

	public static boolean isInitialized()
	{
		return initialized;
	}

	public static String getWrapFromSet(LinkedHashSet<MaterialHelper> matSet){
		StringJoiner value = new StringJoiner(",");

		for (MaterialHelper m : matSet) {
			value.add(m.getName());
		}
		return "[" + value + "]";
	}

	public static String getWrapFromSet(IFlagType<?> base, LinkedHashSet<MaterialHelper> matSet){
		StringJoiner value = new StringJoiner(",");
		value.add(base.getName().toLowerCase());
		for (MaterialHelper m : matSet) {
			value.add(m.getName());
		}
		return "[" + value + "]";
	}
}
