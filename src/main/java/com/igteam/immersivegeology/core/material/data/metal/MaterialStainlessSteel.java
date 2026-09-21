/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.core.lib.shim.MCShims.Ingredients;

import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGAlternatorMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGBallmillMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGBloomeryMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGCentrifugeMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGChemicalReactorMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGCoreDrillMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGFoundryMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGGeothermalExchangerMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGGravitySeparatorMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGPelletizerMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGReverberationFurnaceMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGRotaryKilnMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGSmallChemicalReactorMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGSteamTurbineMultiblock;

import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IngredientWithSize;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IEPlaceholders.IEMultiblocks;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MaterialStainlessSteel extends MaterialMetalAlloy
{
    public MaterialStainlessSteel() {
        super();
        addFlags(BlockCategoryFlags.ENGINEERING_BLOCK, BlockCategoryFlags.FENCE, BlockCategoryFlags.SCAFFOLDING, BlockCategoryFlags.CRATE);
        validMultiblocks.add(() -> IGReverberationFurnaceMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGBloomeryMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGGravitySeparatorMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGPelletizerMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGCoreDrillMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGRotaryKilnMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGChemicalReactorMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGCentrifugeMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGBallmillMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGCrystalizerMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGFoundryMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGGeothermalExchangerMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGSteamTurbineMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGAlternatorMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGSmallChemicalReactorMultiblock.INSTANCE);

        validMultiblocks.add(() -> IEMultiblocks.BLAST_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.SILO);
        validMultiblocks.add(() -> IEMultiblocks.ALLOY_SMELTER);
        validMultiblocks.add(() -> IEMultiblocks.SHEETMETAL_TANK);
        validMultiblocks.add(() -> IEMultiblocks.COKE_OVEN);
        validMultiblocks.add(() -> IEMultiblocks.ADVANCED_BLAST_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.SQUEEZER);
        validMultiblocks.add(() -> IEMultiblocks.MIXER);
        validMultiblocks.add(() -> IEMultiblocks.CRUSHER);
        validMultiblocks.add(() -> IEMultiblocks.ARC_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.ASSEMBLER);
        validMultiblocks.add(() -> IEMultiblocks.AUTO_WORKBENCH);
        validMultiblocks.add(() -> IEMultiblocks.BOTTLING_MACHINE);
        validMultiblocks.add(() -> IEMultiblocks.EXCAVATOR);
        validMultiblocks.add(() -> IEMultiblocks.METAL_PRESS);
        validMultiblocks.add(() -> IEMultiblocks.REFINERY);
        validMultiblocks.add(() -> IEMultiblocks.LIGHTNING_ROD);
        validMultiblocks.add(() -> IEMultiblocks.FEEDTHROUGH);
        validMultiblocks.add(() -> IEMultiblocks.FERMENTER);
        validMultiblocks.add(() -> IEMultiblocks.BUCKET_WHEEL);
        validMultiblocks.add(() -> IEMultiblocks.EXCAVATOR);
        validMultiblocks.add(() -> IEMultiblocks.SAWMILL);
        validMultiblocks.add(() -> IEMultiblocks.DIESEL_GENERATOR);
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.REFINEMENT).create(MetalEnum.Chromium.instance(),
                ItemCategoryFlags.INGOT, 2, ItemCategoryFlags.INGOT,
                8,1, IngredientWithSize.of(new ItemStack(Items.IRON_INGOT, 6)),
                IngredientWithSize.of(new ItemStack(Ingredients.DUST_COKE, 1))).setTimeAndEnergy(400, 204800);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffDBE2E9));
    }
}
