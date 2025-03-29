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
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
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
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialAlumina extends MaterialMineral {

    protected IGRecipeChain bayer_process = new IGRecipeChain(this, "Bayer process", 0);

    public MaterialAlumina() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.END_STONE);
        CONFIG = new MineralConfig(33,20,2,-64,256,3400, 0.5,false,Optional.of(BiomeTags.IS_END), IGGenerationType.BANDED);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x999FAF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Aluminum));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGRecipeNode prep = IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION)
                .create("crushed_ore" + getName() + "_to_dust", getStack(ItemCategoryFlags.CRUSHED_ORE, 1),
                        getStack(ItemCategoryFlags.GRIT, 1), 10000, 100).addToTree(bayer_process);

        IGRecipeNode grit = IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.GRIT, 6000, 100).addToTree(bayer_process, prep);
        IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER).addToTree(bayer_process, prep);

        IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000).addToTree(bayer_process, grit);


        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING)
                .create("alumina_dust_to_compound_aluminum_dust", MetalEnum.Aluminum.getStack(ItemCategoryFlags.COMPOUND_DUST),
                        FluidStack.EMPTY, new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDER), IGLib.COMPOUND_FROM_ACID_AMOUNT),
                        new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT),
                        null, null, 200, 51200).joinBranches(bayer_process, powder_a, powder_b);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "dust_"+getName()+"_to_ingot",
                        getItemTag(ItemCategoryFlags.POWDER), 1,
                        MetalEnum.Aluminum.getStack(ItemCategoryFlags.INGOT),
                        ItemStack.EMPTY,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.POWDER), 1))
                .addExtras(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.POWDER), 0.4f)
                .setTimeAndEnergy(200, 10240).joinBranches(bayer_process, powder_a, powder_b);

    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(bayer_process);
    }
}
