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
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
import com.igteam.immersivegeology.common.item.IGGenericDrillHead.DrillHeadProps;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;

import java.util.function.BiFunction;

public class MaterialHighSpeedSteel extends MaterialMetalAlloy
{

    public MaterialHighSpeedSteel() {
        super();
        removeMaterialFlags(ItemCategoryFlags.values());
        removeMaterialFlags(BlockCategoryFlags.values());
        addFlags(ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET, BlockCategoryFlags.STORAGE_BLOCK, ItemCategoryFlags.DRILL_HEAD);
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.arcSmelting(this, IGStageDesignation.REFINEMENT)
                .create(MetalEnum.Steel.instance(), ItemCategoryFlags.INGOT, 12, //75%
                        ItemCategoryFlags.INGOT, 16, 2,
                        IngredientWithSize.of(MetalEnum.Tungsten.getStack(ItemCategoryFlags.INGOT, 1)), // 5-6%
                        IngredientWithSize.of(MetalEnum.Vanadium.getStack(ItemCategoryFlags.INGOT, 1)), // 3-4%
                        IngredientWithSize.of(MetalEnum.Molybdenum.getStack(ItemCategoryFlags.INGOT, 1)), // 4-5%
                        IngredientWithSize.of(MetalEnum.Manganese.getStack(ItemCategoryFlags.INGOT, 1))
                ).setTimeAndEnergy(400, 204800);
    }

    @Override
    public DrillHeadProps drillHeadInstance()
    {
        return new DrillHeadProps(getName(), getItemTag(ItemCategoryFlags.INGOT), 3, 1, Tiers.NETHERITE, 21.0f, 10, 10000, getTextureLocation(ItemCategoryFlags.DRILL_HEAD));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffDBE2E9));
    }
}
