package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.igteam.immersivegeology.core.registration.IGRegistrationHolder.getBlockRegistryMap;
import static com.igteam.immersivegeology.core.registration.IGRegistrationHolder.getItemRegistryMap;

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

        if(getItemRegistryMap().containsKey(flag.getRegistryKey(this)))
        {
            return IGRegistrationHolder.getItem.apply(flag.getRegistryKey(this));
        }

        IGLib.IG_LOGGER.error("Attempting to get a missing Item? {}", flag.getRegistryKey(this));
        return Items.COOKIE;
    }

    default Item getItem(BlockCategoryFlags flag){
        // Check for edge cases, like in the menu where this can be used to get an Ore Block
        if(flag.equals(BlockCategoryFlags.ORE_BLOCK)){
            return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this, StoneEnum.Shale, OreRichness.RICH)).asItem();
        }

        if(getBlockRegistryMap().containsKey(flag.getRegistryKey(this))) {
            return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this)).asItem();
        }

        IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", flag.getRegistryKey(this));
        return Items.CAKE;
    }


    void addExistingFlag(ModFlags m, ItemCategoryFlags... f);
    void addExistingFlag(ModFlags m, BlockCategoryFlags... f);

    boolean checkExistingImplementation(IFlagType<?> h);

    String getName();
}
