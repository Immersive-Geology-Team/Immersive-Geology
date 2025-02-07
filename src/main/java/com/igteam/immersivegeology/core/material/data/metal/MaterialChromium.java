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
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChromium extends MaterialMetal {

    public MaterialChromium() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
        addFlags(ItemCategoryFlags.COMPOUND_DUST);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xD7B4F3));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();


        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"_oxide_to_slurry",
                MetalEnum.Sodium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.SulfuricAcid.getSlurryWith(MetalEnum.Chromium, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.COMPOUND_DUST, 1)),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(FluidTags.WATER, IGLib.ACID_TO_SLURRY_AMOUNT ), null,200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.SulfuricAcid,
                ItemCategoryFlags.CRYSTAL);


    }
}
