/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;
import java.util.function.BiFunction;

import static com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic.MV_HEAT_CAP;

public class MaterialCalcium extends MaterialMetal {

    //Too reactive, cannot be smelted or used in pure form
    public MaterialCalcium() {
        super();
        removeMaterialFlags(BlockCategoryFlags.SLAB, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE,
                ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, MaterialFlags.IS_MOLTEN_METAL, MaterialFlags.HAS_SLURRY);
        removeMaterialFlags(ItemCategoryFlags.INGOT);
        removeMaterialFlags(ItemCategoryFlags.GEAR);
        removeMaterialFlags(ItemCategoryFlags.PLATE);
        removeMaterialFlags(ItemCategoryFlags.NUGGET);
        removeMaterialFlags(ItemCategoryFlags.CRYSTAL);
        addFlags(ItemCategoryFlags.COMPOUND_DUST);
        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffadbfaa));
    }

    protected IGRecipeChain ammonia_synthesis = new IGRecipeChain(this, "ammonia_synthesis", 0);

    //metal oxide - CaO
    // Powdered Slag - CaCN
    //compound dust - CaCO3
    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGRecipeNode smelting = IGMethodBuilder.arcSmelting(this, IGStageDesignation.SYNTHESIS).create(
                ItemCategoryFlags.METAL_OXIDE, 1,
                ItemCategoryFlags.SLAG, 1, 0,
                new IngredientWithSize(IETags.coalCokeDust, 1)).addToTree(ammonia_synthesis);

        IGMethodBuilder.pulverization(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG).addToTree(ammonia_synthesis);

        IGMethodBuilder.chemical(this, IGStageDesignation.SYNTHESIS).create("ammonia_synthesis_from_"+getName(),
                getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.Ammonia.getFluidStack(IGLib.ACID_RECOVERED_FROM_SLURRY/2),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDERED_SLAG), 8),
                new FluidTagInput(FluidTags.WATER, IGLib.ACID_TO_COMPOUND_AMOUNT/2),
                null,null,200, 51200).addToTree(ammonia_synthesis);

        IGRecipeNode decompose = IGMethodBuilder.decompose(this, IGStageDesignation.PREPARATION).create(
                 ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST,1,
                300).setHVHeat().addToTree(ammonia_synthesis);

        decompose.addChild(smelting);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(ammonia_synthesis);
    }
}
