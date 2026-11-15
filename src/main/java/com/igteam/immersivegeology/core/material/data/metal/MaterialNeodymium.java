/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.function.BiFunction;

public class MaterialNeodymium extends MaterialMetal
{

    public MaterialNeodymium() {
        super();
        removeMaterialFlags(ItemCategoryFlags.WIRE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffAB9CA3));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.chemical(this, IGStageDesignation.PREPARATION).create(getName()+"slurry_to_compound_dust",
                getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.ChemicalWaste.getFluidStack(3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(ItemStack.EMPTY),
                SizedFluidIngredient.of(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                SizedFluidIngredient.of(ChemicalEnum.SulfuricAcid.getSlurryTagWith(MetalEnum.Neodymium), 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                null,200, 51200);

        IGMethodBuilder.decompose(this, IGStageDesignation.ROASTING).create(ItemCategoryFlags.METAL_OXIDE,
                ItemCategoryFlags.COMPOUND_DUST, 1, 300).setMVHeat();

        IGMethodBuilder.chemical(this, IGStageDesignation.PREPARATION).create(getName()+"_metal_oxide_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.HydrofluoricAcid.getSlurryWith(MetalEnum.Neodymium, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.METAL_OXIDE, 1)),
                SizedFluidIngredient.of(ChemicalEnum.HydrofluoricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null,null,200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.HydrofluoricAcid, ItemCategoryFlags.CRYSTAL );
    }
}
