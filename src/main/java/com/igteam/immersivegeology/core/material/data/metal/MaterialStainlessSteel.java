/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialStainlessSteel extends MaterialMetalAlloy
{

    public MaterialStainlessSteel() {
        super();
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.REFINEMENT).create(MetalEnum.Chromium.instance(),
                ItemCategoryFlags.METAL_OXIDE, 1, ItemCategoryFlags.INGOT,
                2, 1f, 1, IngredientWithSize.of(new ItemStack(Items.IRON_INGOT)),
                IngredientWithSize.of(new ItemStack(Ingredients.DUST_COKE))).setTimeAndEnergy(400, 204800);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xDBE2E9));
    }
}
