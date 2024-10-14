/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialAlumina extends MaterialMineral {

    public MaterialAlumina() {
        super();
        removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x999FAF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Aluminum));
    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());

        IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION)
                .create("crushed_ore" + getName() + "_to_dust",getItemTag(ItemCategoryFlags.CRUSHED_ORE), getStack(ItemCategoryFlags.DUST, 1), 10000, 100);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING)
                .create("alumina_dust_to_compound_aluminum_dust", MetalEnum.Aluminum.getStack(ItemCategoryFlags.COMPOUND_DUST), FluidStack.EMPTY, new IngredientWithSize(getItemTag(ItemCategoryFlags.DUST), IGLib.COMPOUND_FROM_ACID_AMOUNT), new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT), null, null, 200, 51200);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "dust_"+getName()+"_to_ingot",
                        getItemTag(ItemCategoryFlags.DUST), 1,
                        MetalEnum.Aluminum.getStack(ItemCategoryFlags.INGOT),
                        MineralEnum.Cryolite.getStack(ItemCategoryFlags.DUST),
                        0.25f,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.DUST), 1))
                .setTimeAndEnergy(200, 10240);

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
