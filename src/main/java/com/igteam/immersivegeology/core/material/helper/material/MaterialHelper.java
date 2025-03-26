/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.BlockEntry;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import com.igteam.immersivegeology.client.helper.IGVeinTextureType;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.item.IGGenericDrillHead.DrillHeadProps;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.ScaffoldingHelper;
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
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

    default boolean canTarnish()
    {
        return false;
    }

    default Item getItem(ItemCategoryFlags flag){
        if(flag == null) {
            flag = ItemCategoryFlags.INGOT;
            IGLib.getNewLogger().error("Attempted to grab an item from registry with a null flag, replacing with INGOT to prevent crash");
        }

        try
        {
            if(Arrays.stream(EnumMetals.values()).anyMatch(e -> e.name().equalsIgnoreCase(getName())))
            {
                switch(flag)
                {
                    case INGOT ->
                    {
                        return Metals.INGOTS.get(EnumMetals.valueOf(getName().toUpperCase())).get().asItem();
                    }
                    case GRIT ->
                    {
                        return Metals.DUSTS.get(EnumMetals.valueOf(getName().toUpperCase())).get().asItem();
                    }
                    case PLATE ->
                    {
                        return Metals.PLATES.get(EnumMetals.valueOf(getName().toUpperCase())).get().asItem();
                    }
                    case NUGGET ->
                    {
                        return Metals.NUGGETS.get(EnumMetals.valueOf(getName().toUpperCase())).get().asItem();
                    }
                    case WIRE ->
                    {
                        if(this.equals(MetalEnum.Aluminum.instance())) return Ingredients.WIRE_ALUMINUM.get();
                        if(this.equals(MetalEnum.Copper.instance())) return Ingredients.WIRE_COPPER.get();
                        if(this.equals(MetalEnum.Lead.instance())) return Ingredients.WIRE_LEAD.get();
                        if(this.equals(MetalEnum.Steel.instance())) return Ingredients.WIRE_STEEL.get();
                    }
                    case POWDER ->
                    {
                        if(this.equals(MineralEnum.Saltpeter.instance())) return Ingredients.DUST_SALTPETER.get();
                    }
                }
            }
        } catch(Exception e) {
            IGLib.IG_LOGGER.info("Unable to find an IE variant for {}", flag.getName());
            IGLib.IG_LOGGER.error("Exception: {}", e.getMessage());
        };

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
        return getFluid(flag, secondary.instance());
    }

    default Fluid getFluid(BlockCategoryFlags flag, MaterialHelper secondary)
    {
        if(flag == null)
        {
            flag = BlockCategoryFlags.SLURRY;
            IGLib.IG_LOGGER.warn("Null Flag Pass for slurry fluid getter, defaulting to SLURRY");
        }

        String id = flag.getRegistryKey(this, secondary);
        if(getFluidRegistryMap().containsKey(id)){
            return IGRegistrationHolder.getFluid.apply(id);
        }
        IGLib.IG_LOGGER.warn("Unable to find Fluid/Slurry for material {}, {}", this, secondary);
        return Fluids.EMPTY;
    }

    TagKey<Fluid> getFluidTag(BlockCategoryFlags type, MaterialHelper... helper);

    default TagKey<Fluid> getSlurryTagWith(BlockCategoryFlags type, MaterialHelper... helper)
    {
        return getFluidTag(type, helper);
    }

    default Item getItem(BlockCategoryFlags flag){
        return this.getBlock(flag).asItem();
    }

    boolean hasFlag(IFlagType<?> category);
    void addExistingFlag(ModFlags m, ItemCategoryFlags... f);
    void addExistingFlag(ModFlags m, BlockCategoryFlags... f);

    boolean checkExistingImplementation(IFlagType<?> h);
    boolean checkExistingImplementation(ModFlags m, IFlagType<?> h);
    boolean weakCheckExistingImplementation(IFlagType<?> h);
    String getName();

    default LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
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
        setupRecipeStages();
        IGStageProvider.add(this, getMaterialStageSet());
    }

    Set<String> logged_recipes = new HashSet<>();
	static void logRecipeStages()
	{
        IGLib.IG_LOGGER.info("{} Recipe Stages have been registered", logged_recipes.size());
	}

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
                    case POWDER ->
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

	default Block getBlock(BlockCategoryFlags flag){
        // Check for edge cases, like in the menu where this can be used to get an Ore Block
        try
        {
            EnumMetals IEMetal = EnumMetals.valueOf(getName().toUpperCase());
            switch(flag.getValue())
            {
                case STORAGE_BLOCK ->
                {
                    return IEBlocks.Metals.STORAGE.get(IEMetal).get();
                }
                case SCAFFOLDING ->
                {
                    return IEBlocks.Metals.SHEETMETAL.get(IEMetal).get();
                }
            }
        } catch(Exception ignored){}

        if(flag.equals(BlockCategoryFlags.ORE_BLOCK)){
            return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this, StoneEnum.Shale, OreRichness.RICH));
        }



        if(getBlockRegistryMap().containsKey(flag.getRegistryKey(this))) {
            return IGRegistrationHolder.getBlock.apply(flag.getRegistryKey(this));
        }

        IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", flag.getRegistryKey(this));
        return Blocks.AIR;
    }

    default IOreBlock getOreBlock(StoneEnum stone, OreRichness richness)
    {
        try
        {
            return (IOreBlock)IGRegistrationHolder.getBlock.apply(BlockCategoryFlags.ORE_BLOCK.getRegistryKey(this, stone, richness));
        } catch(Exception exception)
        {
            //IGLib.IG_LOGGER.warn("No Ore for this combination exists currently: see Mineral[{}] and Stone[{}] and Ore Grade[{}]", getName(), stone.getName(), richness.getSanitizedName());
            return null;
        }
    }

    default IOreBlock getOreBlock(MaterialHelper stone, OreRichness richness)
    {
        try
        {
            return (IOreBlock)IGRegistrationHolder.getBlock.apply(BlockCategoryFlags.ORE_BLOCK.getRegistryKey(this, stone, richness));
        } catch(Exception exception)
        {
            IGLib.IG_LOGGER.error("Unable to get Ore Block: {}", exception.getMessage());
            return null;
        }
    }

    default IGVeinTextureType getVeinTextureType() {return IGVeinTextureType.METALLIC;}
    MaterialInterface<?> getPrimaryProduct();
    MaterialInterface<?> getSecondaryProduct();
    MaterialInterface<?> getTraceProduct(int index);

    default boolean useSedimentaryTextures() { return false;};

    default ScaffoldingHelper getScaffoldingBlock() {
        return new ScaffoldingHelper(this);
    }

    boolean acceptableStoneType(MaterialStone instance);

	default Set<MaterialHelper> getOriginMaterials() {
        return Set.of();
    };

	default DrillHeadProps drillHeadInstance() {return new DrillHeadProps(getName(), IETags.getTagsFor(EnumMetals.STEEL).ingot, 3, 1, Tiers.DIAMOND, 10.0F, 7, 10000, ImmersiveEngineering.rl("item/drill_diesel"));};
}
