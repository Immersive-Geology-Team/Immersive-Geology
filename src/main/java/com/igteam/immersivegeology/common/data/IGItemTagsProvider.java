package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
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
			ArrayList<Item> validBlocks = new ArrayList<>();

			for(EnumMaterials e : EnumMaterials.values())
			{
				Material material = e.material;

				if(material.hasUsetype(type)&&(type.getCategory()==MaterialUseType.UseCategory.RESOURCE_ITEM||type.getCategory()==MaterialUseType.UseCategory.STORAGE_ITEM||
						type.getCategory() == MaterialUseType.UseCategory.ITEM || type.getCategory() == MaterialUseType.UseCategory.TOOLPART_ITEM))
				{
					Item item = IGRegistryGrabber.getIGItem(type, material);
					if(item!=Items.AIR)
					{
						validItems.add(item);
						this.getBuilder(new Wrapper(forgeLoc(type.getTagName()+"/"+material.getName()))).add(item);
					}

				}
				else if(material.hasUsetype(type))
				{
					Item block = IGRegistryGrabber.grabBlock(type, material).asItem();
					if(block != Items.AIR)
					{
						validBlocks.add(block);
						validItems.add(block);
						this.getBuilder(new Wrapper(forgeLoc(type.getTagName()+"/"+material.getName()))).add(block);
					}
				}
			}

			if(!validItems.isEmpty())
				this.getBuilder(new Wrapper(forgeLoc("items/"+type.getName()+"s"))).add(validItems.toArray(new Item[]{}));
//			if(!validBlocks.isEmpty())
//				this.getBuilder(new Wrapper(forgeLoc("blocks/" + type.getName() + "s"))).add(validBlocks.toArray(new Item[]{})); //TODO block tags doesn't register properly
		}

	}
}
