package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

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
						IGLogger.info("Creating Recipe for: "+block);
						ShapedRecipeBuilder.shapedRecipe(block)
								.patternLine("xx")
								.patternLine("xx")
								.key('x', ItemTags.getCollection().get(new ResourceLocation("forge", "chunks/" + resourceItem.getMaterial().getName())))
								.setGroup("bricks")
								.addCriterion("has_chunk", InventoryChangeTrigger.Instance.forItems(item))
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "craft_" + block.name));
					}
					else if(resourceItem.subtype.equals(MaterialUseType.POLISHED_CHUNK))
					{
						IGBaseBlock block = IGRegistryGrabber.grabBlock(MaterialUseType.NORMAL_BRICKS, resourceItem.getMaterial());
						IGLogger.info("Creating Recipe for: "+block);
						ShapedRecipeBuilder.shapedRecipe(block)
								.patternLine("xx")
								.patternLine("xx")
								.key('x', ItemTags.getCollection().get(new ResourceLocation("forge", "polished_chunks/" + resourceItem.getMaterial().getName())))
								.setGroup("bricks")
								.addCriterion("has_chunk", InventoryChangeTrigger.Instance.forItems(item))
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "craft_" + block.name));
					}
					else if(resourceItem.subtype.equals(MaterialUseType.INGOT))
					{
						IGBaseBlock combined = IGRegistryGrabber.grabBlock(MaterialUseType.STORAGE_BLOCK, resourceItem.getMaterial());
						IGLogger.info("Creating Recipe for: "+combined);
						ShapedRecipeBuilder.shapedRecipe(combined)
								.key('x', ItemTags.getCollection().get(new ResourceLocation("forge", "ingots/" + resourceItem.getMaterial().getName())))
								.patternLine("xxx")
								.patternLine("xxx")
								.patternLine("xxx")
								.addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "craft_" + combined.name));
						IGMaterialResourceItem disassemble = (IGMaterialResourceItem)IGRegistryGrabber.getIGItem(MaterialUseType.NUGGET, resourceItem.getMaterial());
						IGLogger.info("Creating Disassembling for: "+disassemble);
						ShapelessRecipeBuilder.shapelessRecipe(disassemble, 9)
								.addIngredient(ItemTags.getCollection().get(new ResourceLocation("forge", "ingots/" + resourceItem.getMaterial().getName())))
								.addCriterion("ingot", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "uncraft_" + resourceItem.itemName));
					}
					else if(resourceItem.subtype.equals(MaterialUseType.NUGGET))
					{
						IGMaterialResourceItem combined = (IGMaterialResourceItem)IGRegistryGrabber.getIGItem(MaterialUseType.INGOT, resourceItem.getMaterial());
						IGLogger.info("Creating Recipe for: "+combined);
						ShapedRecipeBuilder.shapedRecipe(combined)
								.key('x', ItemTags.getCollection().get(new ResourceLocation("forge", "nuggets/" + resourceItem.getMaterial().getName())))
								.patternLine("xxx")
								.patternLine("xxx")
								.patternLine("xxx")
								.addCriterion("nugget", InventoryChangeTrigger.Instance.forItems(resourceItem))
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "craft_" + combined.itemName));
					}
				}
			} catch(Exception e)
			{
				IGLogger.info("Failed to create Recipe: "+e);
			}
		}
		for(IGBaseBlock block : IGContent.registeredIGBlocks.values())
		{
			try
			{
				if(block instanceof IGMaterialBlock)
				{
					IGMaterialBlock resourceBlock = (IGMaterialBlock)block;
					if(resourceBlock.subtype.equals(MaterialUseType.STORAGE_BLOCK))
					{
						IGLogger.info("Block recipe");
						IGMaterialResourceItem disassemble = (IGMaterialResourceItem)IGRegistryGrabber.getIGItem(MaterialUseType.INGOT, resourceBlock.getMaterial());
						IGLogger.info("Creating Disassembling for: "+disassemble);
						ShapelessRecipeBuilder.shapelessRecipe(disassemble, 9)
								.addIngredient(ItemTags.getCollection().get(new ResourceLocation("forge", "storage_blocks/" + resourceBlock.getMaterial().getName())))
								.addCriterion("block", InventoryChangeTrigger.Instance.forItems(resourceBlock)) //recipe unlock criterion
								.build(consumer, new ResourceLocation(ImmersiveGeology.MODID, "uncraft_" + resourceBlock.name));
					}
				}
			} catch(Exception e)
			{
				IGLogger.info("Failed to create Recipe: "+e);
			}
		}
	}
}
