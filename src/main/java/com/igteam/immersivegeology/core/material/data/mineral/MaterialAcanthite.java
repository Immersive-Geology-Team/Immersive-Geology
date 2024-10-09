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
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGChemicalMethod;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
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
    public Set<MaterialInterface<?>> getSourceMaterials()
    {
		return Set.of(MetalEnum.Silver, MetalEnum.Platinum, MetalEnum.Osmium);
    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());

        new IGRecipeStage(this, IGStageDesignation.LEECHING)
        {
            @Override
            protected void describe()
            {
                IGMethodBuilder.chemical(this).create("dust_" + getName() + "_to_slurry", MetalEnum.Platinum.getStack(ItemCategoryFlags.COMPOUND_DUST), ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Silver, 250), IngredientWithSize.of(getStack(ItemCategoryFlags.DUST, 1)), new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 250), null, null, 200, 51200);
            }
        };

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
