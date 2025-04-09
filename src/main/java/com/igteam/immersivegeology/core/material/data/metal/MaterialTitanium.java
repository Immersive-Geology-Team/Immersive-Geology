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
import com.igteam.immersivegeology.common.item.IGGenericDrillHead.DrillHeadProps;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialTitanium extends MaterialMetal {
    protected IGRecipeChain hunter_process = new IGRecipeChain(this, "Hunter Process", 0);

    public MaterialTitanium() {
        super();
        addFlags(BlockCategoryFlags.FENCE, ItemCategoryFlags.DRILL_HEAD);
        removeMaterialFlags(ItemCategoryFlags.CRYSTAL,ItemCategoryFlags.COMPOUND_DUST);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x878681));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGRecipeNode ticl = IGMethodBuilder.chemical(this, IGStageDesignation.PREPARATION).create(getName()+"_metal_oxide_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Titanium, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.METAL_OXIDE, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).addToTree(hunter_process);

        //water is only to dissolve concentrated brine for future electrolysis
        IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create(getName()+"_slury_to_powder_and_brine",
                getStack(ItemCategoryFlags.GRIT, 1),
                ChemicalEnum.Brine.getSlurryWith(MineralEnum.Rocksalt,3*IGLib.ACID_RECOVERED_FROM_SLURRY),
                IngredientWithSize.of(MetalEnum.Sodium.getStack(ItemCategoryFlags.GRIT, 4)),
                new FluidTagInput( ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Titanium),  IGLib.SLURRY_FROM_ACID_AMOUNT),
                new FluidTagInput(FluidTags.WATER, IGLib.SLURRY_FROM_ACID_AMOUNT),
                null, 200, 51200).addToTree(hunter_process, ticl);

        IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create(getName()+"_slury_to_powder_and_brine",
                getStack(ItemCategoryFlags.GRIT, 1),
                ChemicalEnum.Brine.getSlurryWith(MineralEnum.Carnallite,3*IGLib.ACID_RECOVERED_FROM_SLURRY),
                IngredientWithSize.of(MetalEnum.Magnesium.getStack(ItemCategoryFlags.GRIT, 3)),
                new FluidTagInput( ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Titanium),  IGLib.SLURRY_FROM_ACID_AMOUNT),
                new FluidTagInput(FluidTags.WATER, IGLib.SLURRY_FROM_ACID_AMOUNT),
                null, 200, 51200).addToTree(hunter_process, ticl);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(hunter_process);
    }

    @Override
    public DrillHeadProps drillHeadInstance()
    {
        return new DrillHeadProps(getName(), getItemTag(ItemCategoryFlags.INGOT), 5, 1, Tiers.NETHERITE, 9f, 10, 24000, getTextureLocation(ItemCategoryFlags.DRILL_HEAD));
    }
}
