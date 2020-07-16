package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBlockMaterialItem;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;

/**
 * New method
 *
 * @author Pabilo8
 * <p>
 * Original .json batch creation system
 * @author Muddykat
 * @since 15.07.2020
 */
public class IGItemModelProvider extends ItemModelProvider
{

	public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, ImmersiveGeology.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		for(Item item : IGContent.registeredIGItems.values())
		{
			try
			{
				if(item instanceof IGBlockMaterialItem&&((IGBlockMaterialItem)item).getBlock() instanceof IGMaterialBlock)
				{
					IGBlockMaterialItem i = (IGBlockMaterialItem)item;
					IGMaterialBlock b = (IGMaterialBlock)i.getBlock();
					StringBuilder specialName = new StringBuilder();
					for(Material material : b.materials)
					{
						if(material.getSpecialSubtypeModelName(b.subtype)!=null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}
					withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "item/"+b.name).getPath(), new ResourceLocation(ImmersiveGeology.MODID, "block/base/"+i.subtype.getModelPath()+i.subtype.getName()+specialName.toString()));
				}
				else if(item instanceof IGMaterialItem)
				{
					IGMaterialItem i = (IGMaterialItem)item;
					StringBuilder specialName = new StringBuilder();
					for(Material material : i.materials)
					{
						if(material.getSpecialSubtypeModelName(i.subtype)!=null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(i.subtype));
					}
					withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "item/"+i.itemName).getPath(), new ResourceLocation(ImmersiveGeology.MODID, "item/base/"+i.subtype.getModelPath()+i.subtype.getName()+specialName.toString()));
				}
			} catch(Exception ignored)
			{

			}

		}
	}

	@Override
	public String getName()
	{
		return "IG Item Model Provider";
	}
}
