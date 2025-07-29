package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialCryolite extends MaterialMineral {

    protected IGRecipeChain basic_preparation = new IGRecipeChain(this, "Basic Preparation", 0);
    protected IGRecipeChain al_synthesis = new IGRecipeChain(this, "Basic Synthesis", 1);

    public MaterialCryolite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.END_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        CONFIG = new MineralConfig(31,90,1,0,80,2000, 0.8,true,Optional.of(BiomeTags.IS_END), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffC5C5C5));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }
    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());
        IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, new ItemStack(Blocks.GRAVEL), 0.33f, 100, 100);
        IGRecipeNode crushing =  IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100).addToTree(basic_preparation);
        IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.POWDER).addOptionalToTree(basic_preparation);
        IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT,
                ItemCategoryFlags.POWDER, 400, 32000).addToTree(basic_preparation, crushing);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("metal_oxide_"+getName()+"_to_compound_dust",
                MetalEnum.Aluminum.getStack(ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0),
                new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.METAL_OXIDE)),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT),
                null, null, 200, 51200).addToTree(al_synthesis);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("compound_dust_"+MetalEnum.Aluminum.getName()+"_to_cryolite",
                MineralEnum.Cryolite.getStack(ItemCategoryFlags.POWDER, IGLib.DUST_FROM_COMPOUND_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0),
                new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.COMPOUND_DUST), IGLib.COMPOUND_ACID_TO_DUST_AMOUNT),
                new FluidTagInput(ChemicalEnum.HydrofluoricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_DUST_AMOUNT),
                null, null, 200, 51200).addToTree(al_synthesis);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(basic_preparation, al_synthesis);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.22589111f;
    }


    public List<String> getAcceptableDimensions()
    {
        return List.of("minecraft:the_end");
    }
}
