/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialAnatase extends MaterialMineral {

    public MaterialAnatase() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x475B74));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Titanium));
    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());

        IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create( "crushed_ore_" +getName() + "_to_dust",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getStack(ItemCategoryFlags.DUST, 1), 6000, 200);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "dust_" + getName()+"_to_slurry",
                ItemStack.EMPTY,
                new FluidStack(ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Titanium), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.DUST), IGLib.DUST_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null,
                null,
                200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "slurry_" + getName()+"_to_dust",
                MetalEnum.Titanium.getStack(ItemCategoryFlags.DUST, IGLib.COMPOUND_ACID_TO_DUST_AMOUNT),
                new FluidStack(ChemicalEnum.Brine.getFluid(BlockCategoryFlags.FLUID), 250),
                new IngredientWithSize(MetalEnum.Sodium.getItemTag(ItemCategoryFlags.DUST), 1),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Titanium), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new FluidTagInput(FluidTags.WATER, 150),
                null,
                200, 51200);


        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
