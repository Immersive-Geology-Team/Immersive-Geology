/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialIron extends MaterialNativeMetal {

    public MaterialIron() {
        super();
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ItemCategoryFlags.OXIDE_PELLET);

        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.IMMERSIVEENGINEERING, BlockCategoryFlags.SHEETMETAL_BLOCK, BlockCategoryFlags.SHEETMETAL_SLAB);
        addExistingFlag(ModFlags.AD_ASTRA, ItemCategoryFlags.ROD, ItemCategoryFlags.PLATE);
        addExistingFlag(ModFlags.MINECRAFT, ItemCategoryFlags.INGOT, ItemCategoryFlags.NUGGET);
        addExistingFlag(ModFlags.MINECRAFT, BlockCategoryFlags.STORAGE_BLOCK);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.CONFIG = new MaterialMineral.MineralConfig(0, 550,0,0,60,0,0.1, true, Optional.of(Biomes.IS_COLD), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xd8dada));
    }


    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.SulfuricAcid,
                ItemCategoryFlags.CRYSTAL);
    }
}
