package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class IGRecipeProvider extends RecipeProvider
{

	public IGRecipeProvider(DataGenerator generatorIn)
	{
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
	{
		IGRegistryGrabber.getIGItem(MaterialUseType.GEAR, EnumMaterials.Copper.material);
		createBasicRecipes(consumer);
	}

	public void createBasicRecipes(Consumer<IFinishedRecipe> consumer)
	{
		for(Item item : IGContent.registeredIGItems.values())
		{
			try
			{
				if(item instanceof IGMaterialResourceItem)
				{
					IGMaterialResourceItem resourceItem = (IGMaterialResourceItem)item;
					if(resourceItem.subtype.equals(MaterialUseType.CHUNK))
					{
						IGBaseBlock block = IGRegistryGrabber.grabBlock(MaterialUseType.ROUGH_BRICKS, resourceItem.getMaterial());
						ShapedRecipeBuilder.shapedRecipe(block)
								.patternLine("xx")
								.patternLine("xx")
								.key('x', item)
								.setGroup("bricks")
								.addCriterion("has_chunk", InventoryChangeTrigger.Instance.forItems(item))
								.build(consumer);
					}
					else if(resourceItem.subtype.equals(MaterialUseType.POLISHED_CHUNK))
					{
						IGBaseBlock block = IGRegistryGrabber.grabBlock(MaterialUseType.NORMAL_BRICKS, resourceItem.getMaterial());
						ShapedRecipeBuilder.shapedRecipe(block)
								.patternLine("xx")
								.patternLine("xx")
								.key('x', item)
								.setGroup("bricks")
								.addCriterion("has_chunk", InventoryChangeTrigger.Instance.forItems(item))
								.build(consumer);
					}
					else if(resourceItem.subtype.equals(MaterialUseType.INGOT))
					{
						IGBaseBlock combined = IGRegistryGrabber.grabBlock(MaterialUseType.STORAGE, resourceItem.getMaterial());
						System.out.println("Creating Recipe for: "+combined);
						ShapedRecipeBuilder.shapedRecipe(combined)
								.key('x', resourceItem)
								.patternLine("xxx")
								.patternLine("xxx")
								.patternLine("xxx")
								.addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer);
						Item disassemble = IGRegistryGrabber.getIGItem(MaterialUseType.NUGGET, resourceItem.getMaterial());
						ShapelessRecipeBuilder.shapelessRecipe(disassemble, 9)
								.addIngredient(resourceItem)
								.addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer);
					}
					else if(resourceItem.subtype.equals(MaterialUseType.NUGGET))
					{
						Item combined = IGRegistryGrabber.getIGItem(MaterialUseType.INGOT, resourceItem.getMaterial());
						ShapedRecipeBuilder.shapedRecipe(combined)
								.key('x', resourceItem)
								.patternLine("xxx")
								.patternLine("xxx")
								.patternLine("xxx")
								.addCriterion("nugget", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer);
					}
					else if(resourceItem.subtype.equals(MaterialUseType.STORAGE))
					{
						Item disassemble = IGRegistryGrabber.getIGItem(MaterialUseType.INGOT, resourceItem.getMaterial());
						ShapelessRecipeBuilder.shapelessRecipe(disassemble, 9)
								.addIngredient(resourceItem.getItem())
								.addCriterion("block", InventoryChangeTrigger.Instance.forItems(resourceItem)) //recipe unlock criterion
								.build(consumer);
					}
				}
			} catch(Exception e)
			{
				System.out.println("Failed to create Recipe: "+e);
			}
		}
	}
}
