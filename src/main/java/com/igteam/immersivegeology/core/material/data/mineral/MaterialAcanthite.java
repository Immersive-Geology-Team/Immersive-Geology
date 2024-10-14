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
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

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
        LinkedHashSet<MaterialInterface<?>> materials = new LinkedHashSet<>();
        materials.add(MetalEnum.Silver);
        materials.add(MetalEnum.Platinum);
        materials.add(MetalEnum.Osmium);
		return materials;
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

        IGMethodBuilder.blasting(this, IGStageDesignation.BLASTING).create("slag_" + getName() + "_to_metal", getItemTag(ItemCategoryFlags.SLAG), getProductionMaterial().getStack(ItemCategoryFlags.INGOT));

        IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create("slag_"+getName() +"_to_dust", getItemTag(ItemCategoryFlags.SLAG), getStack(ItemCategoryFlags.DUST, 1), getItemTag(ItemCategoryFlags.DUST), 3000, 200, 0.25f);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "grit_to_silver_slurry",
                getByproductMaterial().getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.HydrochloricAcid.getSlurryWith(getProductionMaterial(), IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.DUST, IGLib.DUST_TO_SLURRY_AMOUNT)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT), null, null,
                200, 51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create("slurry_" + getProductionMaterial().getName() + "_to_crystal", getProductionMaterial().getStack(ItemCategoryFlags.CRYSTAL), ChemicalEnum.HydrochloricAcid.getSlurryTagWith(getProductionMaterial()), IGLib.SLURRY_TO_CRYSTAL_MB, 300, 38400);

        IGMethodBuilder.separating(this, IGStageDesignation.PURIFICATION).create(getByproductMaterial().getItemTag(ItemCategoryFlags.COMPOUND_DUST),
                getByproductMaterial().getStack(ItemCategoryFlags.DUST), getTraceMaterials(2).getStack(ItemCategoryFlags.DUST), 0.2f, 60, 100);

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
