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
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSilver extends MaterialNativeMetal {

    public MaterialSilver() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(12,95,1,-32,50,500,0.5,false,Optional.empty(), IGGenerationType.DEFAULT);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0xC0C0C0),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x2E2E2E)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.HydrochloricAcid,
                ItemCategoryFlags.CRYSTAL);

        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT));

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 2,
                ItemCategoryFlags.INGOT, 1, 60);

        IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.SLURRY,
                ItemStack.EMPTY,
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Silver, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.CRUSHED_ORE, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200);


    }
}
