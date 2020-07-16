package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;

public class IGRecipeProvider
{
    public IGRecipeProvider(DataGenerator gen)
    {
        super();
    }

    public void createBasicRecipes()
    {
        for(Item item : IGContent.registeredIGItems.values())
        {
            try {
                if (item instanceof IGMaterialResourceItem) {
                    IGMaterialResourceItem resourceItem = (IGMaterialResourceItem) item;
                    if (resourceItem.getUseType().equals(MaterialUseType.CHUNK)) {

                    }
                    if (resourceItem.getUseType().equals(MaterialUseType.POLISHED_CHUNK)) {

                    }
                }
            } catch(Exception ignored)
            {

            }
        }
    }
}
