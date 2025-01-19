/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGArcSmeltingMethod;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialHastelloy extends MaterialMetalAlloy
{

    public MaterialHastelloy() {
        super();
        addFlags(ItemCategoryFlags.MECHANICAL_COMPONENT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xb6afa9));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.arcSmelting(this, IGStageDesignation.SYNTHESIS).create(MetalEnum.Nickel.instance(), ItemCategoryFlags.METAL_OXIDE, 8, ItemCategoryFlags.INGOT, 8, 0f, 0,
                IngredientWithSize.of(MetalEnum.Chromium.getStack(ItemCategoryFlags.METAL_OXIDE, 4)),
                IngredientWithSize.of(MetalEnum.Molybdenum.getStack(ItemCategoryFlags.METAL_OXIDE, 2)),
                IngredientWithSize.of(MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE, 1)),
                IngredientWithSize.of(MetalEnum.Tungsten.getStack(ItemCategoryFlags.METAL_OXIDE, 1))).setTimeAndEnergy(1600, 819200);
    }
}
