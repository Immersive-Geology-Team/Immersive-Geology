/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialVanadium extends MaterialMetal {

    public MaterialVanadium() {
        super();
        removeMaterialFlags(ItemCategoryFlags.PELLET);
        removeMaterialFlags(ItemCategoryFlags.SLAG);
        removeMaterialFlags(ItemCategoryFlags.POWDERED_SLAG);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff8e1e1d));
    }

    @Override
    public boolean useColumnBlockStyle(IFlagType<?> flag)
    {
        return flag.equals(BlockCategoryFlags.STORAGE_BLOCK);
    }

    @Override
    public void setupRecipeStages()
    {
        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "dust_"+getName()+"_to_ingot",
                        getItemTag(ItemCategoryFlags.METAL_OXIDE), 1,
                        getStack(ItemCategoryFlags.INGOT, 1),
                        ItemStack.EMPTY,
                        new IngredientWithSize(MetalEnum.Magnesium.getItemTag(ItemCategoryFlags.GRIT), 1))
                .addExtras(MetalEnum.Magnesium.getItemTag(ItemCategoryFlags.METAL_OXIDE), 0.9f)
                .setTimeAndEnergy(200, 10240);
    }
}
