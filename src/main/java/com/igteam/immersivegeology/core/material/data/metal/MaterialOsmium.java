/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialOsmium extends MaterialNativeMetal
{

    public MaterialOsmium() {
        super();
        this.CONFIG = new MaterialMineral.MineralConfig(0,0,0,0,0,0,0, false, Optional.empty(), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x8A9A9A));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.decompose(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.COMPOUND_DUST, 1, 140,1024);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                ItemCategoryFlags.COMPOUND_DUST, BlockCategoryFlags.SLURRY,
                MetalEnum.Platinum.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.Ammonia.getSlurryWith(MetalEnum.Osmium, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.COMPOUND_DUST, 1)),
                new FluidTagInput(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create(getName()+"_amide_to_grit",
                getStack(ItemCategoryFlags.GRIT, 1),
                ChemicalEnum.ChemicalWaste.getFluidStack(IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.Ammonia.getSlurryTagWith(MetalEnum.Osmium), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                200, 51200);


    }


}
