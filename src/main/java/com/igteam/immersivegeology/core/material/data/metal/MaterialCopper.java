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
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialCopper extends MaterialNativeMetal {

    public MaterialCopper() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ItemCategoryFlags.OXIDE_PELLET);

        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.STAIRS, BlockCategoryFlags.SLAB);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.WIRE, ItemCategoryFlags.POWDER, ItemCategoryFlags.PLATE, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(10,70,1,0,128,2000,0.5, true,Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.DEFAULT);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0xB66E3D),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x3D8B8B)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 2,
                this, ItemCategoryFlags.INGOT, 1, 200);

        IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create( ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.GRIT ,6000, 100);

        IGMethodBuilder.chemical(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.SLURRY,
                ItemStack.EMPTY,
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Copper, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.CRUSHED_ORE, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.HydrochloricAcid, ItemCategoryFlags.CRYSTAL);

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.GRIT,
                ItemCategoryFlags.INGOT, 120);

    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }
}
