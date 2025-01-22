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
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
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

public class MaterialTungsten extends MaterialMetal {

    public MaterialTungsten() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x444D6A));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        //Direct Leaching in HCL
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"slurry_to_compound_dust",
                getStack(ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT),
                new FluidStack(Fluids.WATER, 250), //TODO -- Chemical waste
                IngredientWithSize.of (ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Tungsten), IGLib.SLURRY_TO_CRYSTAL_MB),
                //TODO AMMONIA
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.SLURRY_TO_CRYSTAL_MB),
                null,200, 51200);

        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(ItemCategoryFlags.COMPOUND_DUST,
                ItemCategoryFlags.METAL_OXIDE, 1, 300, 153600);


    }
}
