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
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.BiFunction;

public class MaterialAluminum extends MaterialMetal {

    protected IGRecipeChain hall_heroit_process = new IGRecipeChain(this, "Hall-Heroit process", 0);

    public MaterialAluminum()
    {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, BlockCategoryFlags.ENGINEERING_BLOCK);

        removeMaterialFlags(ItemCategoryFlags.CRYSTAL);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT,
                ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE,
                ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK,
                BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SHEETMETAL_STAIRS,
                BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB, BlockCategoryFlags.SHEETMETAL_SLAB);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffd0d5db));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST,
                1, 300, 153600).addToTree(hall_heroit_process);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "aluminium_oxide_to_ingot",
                        getItemTag(ItemCategoryFlags.METAL_OXIDE), 1,
                        MetalEnum.Aluminum.getStack(ItemCategoryFlags.INGOT),
                        ItemStack.EMPTY,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.POWDER), 1))
                .addExtras(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.POWDER), 0.5f)
                .setTimeAndEnergy(400, 204800).addToTree(hall_heroit_process);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(hall_heroit_process);
    }
}
