/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.register.IEItems;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGStageProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

import static com.igteam.immersivegeology.core.registration.IGRegistrationHolder.*;

public interface MaterialHelper {

    default ItemStack getStack(IFlagType<?> unknownFlag, int amount) {
        if(unknownFlag instanceof ItemCategoryFlags flag)return new ItemStack(getItem(flag), amount);
        if(unknownFlag instanceof BlockCategoryFlags flag)return new ItemStack(getItem(flag), amount);
        IGLib.IG_LOGGER.error("{} is not an Item or Block Flag", unknownFlag.getName());
        return ItemStack.EMPTY;
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

    default Fluid getFluid(BlockCategoryFlags flag)
    {
        if(flag == null)
        {
            flag = BlockCategoryFlags.FLUID;
            IGLib.IG_LOGGER.warn("Null Flag Pass for fluid getter, defaulting to FLUID");
        }

        String id = flag.getRegistryKey(this);
        if(getFluidRegistryMap().containsKey(id)){
            return IGRegistrationHolder.getFluid.apply(id);
        }
        IGLib.IG_LOGGER.warn("Unable to find Fluid for material {}", id);
        return Fluids.EMPTY;
    }

    default Fluid getFluid(BlockCategoryFlags flag, MaterialInterface<?> secondary)
    {
        if(flag == null)
        {
            flag = BlockCategoryFlags.SLURRY;
            IGLib.IG_LOGGER.warn("Null Flag Pass for slurry fluid getter, defaulting to SLURRY");
        }

        String id = flag.getRegistryKey(this, secondary.instance());
        if(getFluidRegistryMap().containsKey(id)){
            return IGRegistrationHolder.getFluid.apply(id);
        }
        IGLib.IG_LOGGER.warn("Unable to find Fluid/Slurry for material {}, {}", this, secondary);
        return Fluids.EMPTY;
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
    boolean checkExistingImplementation(ModFlags m, IFlagType<?> h);

    String getName();

    default LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>();
    };

    void addStage(IGRecipeStage stage);
    Set<IGRecipeStage> getMaterialStageSet();

    default Set<IGRecipeStage> getStageSet()
    {
        return IGStageProvider.get(this);
    }

    void setupRecipeStages();

    default void buildRecipe()
    {
        IGLib.IG_LOGGER.debug("Building {} Recipe Stages", getName());
        setupRecipeStages();
        IGStageProvider.add(this, getMaterialStageSet());
    };

    default TagKey<Item> getItemTag(IFlagType<?> unknownFlag)
    {
        try
        {
            if(!(unknownFlag instanceof ItemCategoryFlags))
                throw (new IllegalArgumentException("Non Item Category Flag Parsed to getItemTag"));
            ItemCategoryFlags flag = (ItemCategoryFlags)unknownFlag;

            try
            {
                EnumMetals IEMetal = EnumMetals.valueOf(getName().toUpperCase());
                IETags.MetalTags ieMetalTags = IETags.getTagsFor(IEMetal);
                switch(flag.getValue())
                {
                    case INGOT ->
                    {
                        return ieMetalTags.ingot;
                    }
                    case DUST ->
                    {
                        return ieMetalTags.dust;
                    }
                    case NUGGET ->
                    {
                        return ieMetalTags.nugget;
                    }
                    case PLATE ->
                    {
                        return ieMetalTags.plate;
                    }
                }
            } catch(Exception ignored){}

            HashMap<String, TagKey<Item>> data_map = IGTags.ITEM_TAG_HOLDER.get(flag);
            LinkedHashSet<MaterialHelper> material_set = new LinkedHashSet<>(Collections.singletonList(this));
            String key = IGTags.getWrapFromSet(material_set);
            return data_map.get(key);
        } catch(Exception e)
        {
            IGLib.IG_LOGGER.error(e.getLocalizedMessage());
        }
        IGLib.IG_LOGGER.warn("Null Tag Returned for {} {}", getName(), unknownFlag);
        return null;
    }
}
