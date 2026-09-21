package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.FluidTagInput;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IngredientWithSize;

import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
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
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialZircon extends MaterialMineral {

    protected IGRecipeChain caustic_fusion = new IGRecipeChain(this, "Caustic Fusion", 0);

    public MaterialZircon() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addFlags(ItemCategoryFlags.SEDIMENT);
        // TODO Remove fully probably
        CONFIG = new MineralConfig(30, 50, 1, -64, 200, 1000, 0.7,false, Optional.empty(), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff8B2E1D));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Zirconium));
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());

        IGRecipeNode wash = IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, new ItemStack(Blocks.GRAVEL),
                0.33f, 100, 100).addToTree(caustic_fusion);

        IGRecipeNode grit = IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100).addToTree(caustic_fusion, wash);

        IGRecipeNode sand_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 400, 32000).addToTree(caustic_fusion, grit);

        IGRecipeNode sand_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER).addToTree(caustic_fusion, wash);

        IGRecipeNode digestion = IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "ore_powder_" + getName() + "_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.SodiumHydroxide.getCloudySlurryWith(MineralEnum.Zircon, IGLib.SLURRY_FROM_ACID_AMOUNT),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDER), 1),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).joinBranches(caustic_fusion, sand_a, sand_b);

        IGRecipeNode settling = IGMethodBuilder.centrifuge(this, IGStageDesignation.REFINEMENT).create(
                ChemicalEnum.SodiumHydroxide.getCloudySlurryTagWith(MineralEnum.Zircon), IGLib.SLURRY_TO_CRYSTAL_MB,
                this, ItemCategoryFlags.SEDIMENT, 1,
                ChemicalEnum.SodiumHydroxide.getFluid(BlockCategoryFlags.FLUID), IGLib.ACID_RECOVERED_FROM_SLURRY,
                null, 0,
                1200, 614400).addToTree(caustic_fusion, digestion);

        IGRecipeNode chlorination = IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create(
                getName() + "_sediment_to_compound_dust",
                MetalEnum.Zirconium.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.ChemicalWaste.getFluidStack(IGLib.ACID_TO_SLURRY_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.SEDIMENT, 1)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).addToTree(caustic_fusion, settling);

        IGMethodBuilder.decompose(this, IGStageDesignation.REFINEMENT).create(
                "compound_dust_" + MetalEnum.Zirconium.getName() + "_to_metal_oxide",
                MetalEnum.Zirconium.getStack(ItemCategoryFlags.METAL_OXIDE, 1),
                MetalEnum.Zirconium.getItemTag(ItemCategoryFlags.COMPOUND_DUST), 1, 300)
                .setHVHeat().addToTree(caustic_fusion, chlorination);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(caustic_fusion);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.19122314f;
    }
}
