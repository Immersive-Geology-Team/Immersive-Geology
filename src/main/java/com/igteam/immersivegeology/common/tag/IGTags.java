/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.tag;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.tags.ITag;

import java.util.*;

public class IGTags
{
	public static HashMap<IFlagType<?>, HashMap<String, TagKey<Item>>> ITEM_TAG_HOLDER = new HashMap<>();

	public static void initialize()
	{
		IGLib.IG_LOGGER.info("Initializing Immersive Geology Tags");
		for(ItemCategoryFlags itemFlag : ItemCategoryFlags.values())
		{
			ITEM_TAG_HOLDER.put(itemFlag, new HashMap<>());

			for(MaterialInterface<?> materialInterface : IGLib.getGeologyMaterials())
			{
				if(materialInterface.hasFlag(itemFlag)){
					createWrapperForCategory(itemFlag, materialInterface.instance());
				}
			}
		}
	}

	private static void createWrapperForCategory(IFlagType<?> category, GeologyMaterial... materials)
	{
		if(Arrays.stream(materials).anyMatch(m -> m.hasFlag(category)))
		{
			if(category.getValue() instanceof ItemCategoryFlags itemFlag)
			{
				HashMap<String, TagKey<Item>> map = ITEM_TAG_HOLDER.get(itemFlag);
				LinkedHashSet<GeologyMaterial> materialSet = new LinkedHashSet<>(Arrays.asList(materials));

				map.put(getWrapFromSet(materialSet), ItemTags.create(wrapCategory(itemFlag, materialSet)));
			}
		}
	}

	private static ResourceLocation wrapCategory(IFlagType<?> category, Set<GeologyMaterial> materialSet)
	{
		StringJoiner material_set_name = new StringJoiner("_");
		materialSet.forEach((m -> material_set_name.add(m.getName())));

		return new ResourceLocation("forge", category.getName() + category.getTagPrefix() + "/" + material_set_name);
	}



	public static String getWrapFromSet(LinkedHashSet<GeologyMaterial> matSet){
		StringJoiner value = new StringJoiner(",");

		for (GeologyMaterial m : matSet) {
			value.add(m.getName());
		}
		return "[" + value + "]";
	}
}
