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
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialZinc extends MaterialMetal {

    public MaterialZinc() {
        super();
        addFlags(ItemCategoryFlags.OXIDE_PELLET);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xd0d5db));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        //	public void create(String name, ItemStack itemOutput, FluidStack fluidOutput,
        //	IngredientWithSize itemIn, FluidTagInput fluidInA, FluidTagInput fluidInB, FluidTagInput fluidInC, int time, int energy){
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"_oxide_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.SulfuricAcid.getSlurryWith(MetalEnum.Zinc, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.METAL_OXIDE, 1)),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null,200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                ChemicalEnum.SulfuricAcid,
                ItemCategoryFlags.CRYSTAL);

        IGMethodBuilder.basicSmelting(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.INGOT);

    }


    @Override
    public Set<MaterialHelper> getOriginMaterials()
    {
        return Set.of(MineralEnum.Smithsonite.instance(), MineralEnum.Sphalerite.instance());
    }
}
