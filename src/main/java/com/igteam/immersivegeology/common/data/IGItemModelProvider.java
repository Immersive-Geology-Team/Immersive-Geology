package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBlockMaterialItem;
import com.igteam.immersivegeology.common.blocks.IGLayerBase;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.plant.IGLogBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;

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
				if(item instanceof IGBlockMaterialItem&&((IGBlockMaterialItem)item).getBlock() instanceof IGLayerBase)
				{
					IGBlockMaterialItem i = (IGBlockMaterialItem) item;
					IGLayerBase b = (IGLayerBase) i.getBlock();
					StringBuilder specialName = new StringBuilder();
					for (Material material : b.materials) {
						if (material.getSpecialSubtypeModelName(b.subtype) != null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}
					String builder_name = new ResourceLocation(ImmersiveGeology.MODID, "item/"+b.name).getPath();

					withExistingParent(builder_name, new ResourceLocation(ImmersiveGeology.MODID, "block/base/layer/"+i.subtype.getName()+specialName.toString() + "_height2")).texture("base","block/greyscale/layer/" + b.getMaterial().getName());
				} else if(item instanceof IGBlockMaterialItem&&((IGBlockMaterialItem)item).getBlock() instanceof IGMaterialBlock)
				{
					IGBlockMaterialItem i = (IGBlockMaterialItem)item;
					IGMaterialBlock b = (IGMaterialBlock)i.getBlock();
					if(b instanceof IGLogBlock)
					{

					} else
					{
						StringBuilder specialName = new StringBuilder();
						for(Material material : b.materials)
						{
							if(material.getSpecialSubtypeModelName(b.subtype)!=null)
								specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
						}

						String builder_name = new ResourceLocation(ImmersiveGeology.MODID, "item/"+b.name).getPath();
						withExistingParent(builder_name, new ResourceLocation(ImmersiveGeology.MODID, "block/base/"+i.subtype.getModelPath()+i.subtype.getName()+specialName.toString()));
						String type_name = specialName.toString();
						if(type_name.length() > 1) {
							getBuilder(builder_name).texture("ore", "immersivegeology:block/greyscale/rock/ore_bearing/" + type_name.substring(1, type_name.length()) + "/" + type_name.substring(1, type_name.length()) + "_normal");
							getBuilder(builder_name).texture("base", "immersivegeology:block/greyscale/rock/rock" + type_name);

							getBuilder(builder_name).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("base").tintindex(0).uvs(0,0,16,16)));
							getBuilder(builder_name).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("ore").tintindex(1).uvs(0,0,16,16)));
						}
					}
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
			} catch(Exception e)
			{
				IGLogger.info("Failed to create Item Model:"+e);
			}

		}
	}

	@Override
	public String getName()
	{
		return "IG Item Model Provider";
	}
}
