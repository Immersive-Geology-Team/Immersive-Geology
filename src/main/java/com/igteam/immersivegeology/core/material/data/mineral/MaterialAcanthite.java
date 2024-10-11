/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Metals;
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
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGBlastingMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGChemicalMethod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialAcanthite extends MaterialMineral {

    public MaterialAcanthite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        addFlags(ItemCategoryFlags.SLAG);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x83C4EA));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {

		return new LinkedHashSet<>(Set.of(MetalEnum.Silver, MetalEnum.Platinum, MetalEnum.Osmium));
    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());


        IGMethodBuilder.roast(this, IGStageDesignation.ROASTING).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,   // Input
                ItemCategoryFlags.SLAG, 1,         // Output
                1000,                                          // Roasting Time
                200                                            // Sulfur Dioxide Output Amount
        );

        IGMethodBuilder.blasting(this, IGStageDesignation.BLASTING).create("slag_" + getName() + "_to_metal", getItemTag(ItemCategoryFlags.SLAG), new ItemStack(Metals.INGOTS.get(EnumMetals.SILVER).asItem()));

        IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create("slag_"+getName() +"_to_dust", getItemTag(ItemCategoryFlags.SLAG), getStack(ItemCategoryFlags.DUST, 1), getItemTag(ItemCategoryFlags.DUST), 3000, 200, 0.25f);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "grit_to_silver_slurry",
                MetalEnum.Platinum.getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Silver, 750),
                IngredientWithSize.of(getStack(ItemCategoryFlags.DUST, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 750), null, null,
                200, 51200);

        IGMethodBuilder.crystalize(this, IGStageDesignation.CRYSTALLIZATION).create("slurry_silver_to_crystal", MetalEnum.Silver.getStack(ItemCategoryFlags.CRYSTAL), ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Silver), 250, 300, 38400);

        IGMethodBuilder.separating(this, IGStageDesignation.PURIFICATION).create(MetalEnum.Platinum.getItemTag(ItemCategoryFlags.COMPOUND_DUST),
                MetalEnum.Platinum.getStack(ItemCategoryFlags.DUST), MetalEnum.Osmium.getStack(ItemCategoryFlags.DUST), 0.2f, 60, 100);

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
