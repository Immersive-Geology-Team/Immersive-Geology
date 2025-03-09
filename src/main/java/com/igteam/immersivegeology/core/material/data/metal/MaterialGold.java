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
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialGold extends MaterialNativeMetal {

    public MaterialGold() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);

        this.acceptableStoneTypes.add(StoneFormation.NETHER_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.CONFIG = new MaterialMineral.MineralConfig(8,75,1,0,120,500,0.5, false, Optional.empty(), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xFFD700));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT));

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 2,
                ItemCategoryFlags.INGOT, 1, 200);

        IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.SLURRY,
                ItemStack.EMPTY,
                ChemicalEnum.AquaRegia.getSlurryWith(MetalEnum.Gold, 2*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.CRUSHED_ORE, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                 null, 200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.AquaRegia, ItemCategoryFlags.CRYSTAL);
    }
}
