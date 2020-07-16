package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
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
        createBasicRecipes(consumer);
    }

    public void createBasicRecipes(Consumer<IFinishedRecipe> consumer)
    {
        for(Item item : IGContent.registeredIGItems.values())
        {
            try {
                if (item instanceof IGMaterialResourceItem) {
                    IGMaterialResourceItem resourceItem = (IGMaterialResourceItem) item;
                    if(resourceItem.subtype.equals(MaterialUseType.CHUNK))
                    {
                        IGBaseBlock block = IGRegistryGrabber.grabBlock(MaterialUseType.COBBLESTONE, resourceItem.getMaterial());
                        ShapedRecipeBuilder.shapedRecipe(block)
                                .patternLine("xx")
                                .patternLine("xx")
                                .key('x', item)
                                //.addCriterion("cobblestone", InventoryChangeTrigger.Instance.forItems(Blocks.COBBLESTONE)) recipe unlock criterion
                                .build(consumer);
                    }
                    if(resourceItem.subtype.equals(MaterialUseType.ORE_CHUNK))
                    {

                    }
                }
            } catch(Exception ignored)
            {

            }
        }
    }
}
