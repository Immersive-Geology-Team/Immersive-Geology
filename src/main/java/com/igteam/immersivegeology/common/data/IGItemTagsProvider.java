package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags.Wrapper;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * @author Pabilo8
 * @since 14.07.2020
 */
public class IGItemTagsProvider extends net.minecraft.data.ItemTagsProvider
{
	public IGItemTagsProvider(DataGenerator gen)
	{
		super(gen);
	}

	//private HashMap<MaterialUseType, Wrapper> wrapperMap = new HashMap<>();

	private static ResourceLocation forgeLoc(String path)
	{
		return new ResourceLocation("forge", path);
	}

	@Override
	protected void registerTags()
	{

		for(MaterialUseType type : MaterialUseType.values())
		{
			ArrayList<Item> validItems = new ArrayList<>();

			for(EnumMaterials e : EnumMaterials.values())
			{
				Material material = e.material;

				if(material.hasSubtype(type))
				{
					Item item = IGRegistryGrabber.getIGItem(type, material);
					if(item!=Items.AIR)
					{
						validItems.add(item);
						this.getBuilder(new Wrapper(forgeLoc(type.getTagName()+"/"+material.getName()))).add(item);
					}

				}
			}

			if(!validItems.isEmpty())
				this.getBuilder(new Wrapper(forgeLoc("items/"+type.getName()+"s"))).add(validItems.toArray(new Item[]{}));
		}

	}
}
