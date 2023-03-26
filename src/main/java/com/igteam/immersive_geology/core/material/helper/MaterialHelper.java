package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.NonNull;

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
            ImmersiveGeology.getNewLogger().error("Attempted to grab an item from registry with a null flag, replacing with INGOT to prevent crash");
        }

        return IGRegistrationHolder.getItem.apply(flag.getRegistryKey(this));
    }

    default Item getItem(BlockCategoryFlags flag){
        return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this)).asItem();
    }
    String getName();
}
