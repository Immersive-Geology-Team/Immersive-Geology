/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraftforge.common.Tags.Biomes;

import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialLead extends MaterialNativeMetal
{
    public MaterialLead() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.INGOT, ItemCategoryFlags.PLATE, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.NUGGET, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.STORAGE_BLOCK, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SLAB, BlockCategoryFlags.STAIRS);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(12,99,1,0,50,5,0.5, true, Optional.of(Biomes.IS_WET), IGGenerationType.DEFAULT);
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 2,
                ItemCategoryFlags.INGOT, 1, 60);

        //HANDWAVE
        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.HydrochloricAcid,
                ItemCategoryFlags.CRYSTAL);

    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x444f53));
    }
}
