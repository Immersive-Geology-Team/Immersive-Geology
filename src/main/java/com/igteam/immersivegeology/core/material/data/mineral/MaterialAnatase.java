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
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialAnatase extends MaterialMineral {

    public MaterialAnatase() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.END_STONE);
        CONFIG = new MineralConfig(32,70,1,-64,70,5500,0.5, true,Optional.of(BiomeTags.IS_END), IGGenerationType.DEFAULT);
    }
    protected IGRecipeChain hunter_process = new IGRecipeChain(this, "Hunter Process", 0);

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x475B74));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Titanium));
    }

    @Override
    public void setupRecipeStages()
    {
        IGRecipeNode prep = IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.DIRTY_CRUSHED_ORE,
                ItemCategoryFlags.CRUSHED_ORE, new ItemStack(Blocks.GRAVEL), 0.33f, 100, 100).addToTree(hunter_process);

        IGRecipeNode grit = IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.GRIT, 6000, 100).addToTree(hunter_process, prep);
        IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER).addToTree(hunter_process, prep);

        IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000).addToTree(hunter_process, grit);

        IGRecipeNode ticl = IGMethodBuilder.chemical(this, IGStageDesignation.PREPARATION).create(getName()+"_metal_oxide_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Titanium, IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDER, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).joinBranches(hunter_process, powder_a, powder_b);

    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(hunter_process);
    }
}
