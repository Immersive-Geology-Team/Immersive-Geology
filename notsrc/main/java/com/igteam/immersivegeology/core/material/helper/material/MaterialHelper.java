package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface MaterialHelper {

    default ItemStack getStack(ItemCategoryFlags flag) {
        return new ItemStack(getItem(flag));
    }

    default ItemStack getStack(BlockCategoryFlags flag) {
        return new ItemStack(getItem(flag));
    }

    default Item getItem(ItemCategoryFlags flag){
        if(flag == null) {
            flag = ItemCategoryFlags.INGOT;
            IGLib.getNewLogger().error("Attempted to grab an item from registry with a null flag, replacing with INGOT to prevent crash");
        }

        return IGRegistrationHolder.getItem.apply(flag.getRegistryKey(this));
    }

    default Item getItem(BlockCategoryFlags flag){
        return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this)).asItem();
    }

    String getName();
}
