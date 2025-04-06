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
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;

import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialPlatinum extends MaterialNativeMetal {

    public MaterialPlatinum() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(12,90,1,10,150,400,0.5, true,Optional.of(BiomeTags.IS_END), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xe7e7f7));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                ItemCategoryFlags.COMPOUND_DUST, BlockCategoryFlags.FLUID,
                MetalEnum.Platinum.getStack(ItemCategoryFlags.GRIT, 1),
                ChemicalEnum.ChemicalWaste.getFluidStack(IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.COMPOUND_DUST, 1)),
                new FluidTagInput(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), 125),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125), 200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.SLURRY,
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.AquaRegia.getSlurryWith(MetalEnum.Platinum, 2*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.CRUSHED_ORE, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, 200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.SLURRY,
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.AquaRegia.getSlurryWith(MetalEnum.Platinum, 2*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.CRUSHED_ORE, 1)),
                new FluidTagInput(ChemicalEnum.AquaRegia.getFluidTag(BlockCategoryFlags.FLUID), 2*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.025939941f;
    }
}
