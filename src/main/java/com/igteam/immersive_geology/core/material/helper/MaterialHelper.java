package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface MaterialHelper {

    default ItemStack getStack(ItemCategoryFlags flag) {
        return new ItemStack(getItem(flag));
    }

    default ItemStack getStack(BlockCategoryFlags flag) {
        return new ItemStack(getItem(flag));
    }

    default Item getItem(ItemCategoryFlags flag){
        if(flag == null) flag = ItemCategoryFlags.INGOT;

        ImmersiveGeology.getNewLogger().info("Flag: " + flag.name());
        ImmersiveGeology.getNewLogger().info("Material: " + getName());
        return IGRegistrationHolder.ITEM_REGISTRY.get(flag.getRegistryKey(this)).get();
    }

    default Item getItem(BlockCategoryFlags flag){
        return IGRegistrationHolder.BLOCK_REGISTRY.get(flag.getRegistryKey(this)).get().asItem();
    }
    String getName();
}
