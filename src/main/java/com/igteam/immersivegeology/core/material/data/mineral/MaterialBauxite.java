package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialBauxite extends MaterialMineral {

    protected IGRecipeChain bayer_process = new IGRecipeChain(this, "Bayer process", 0);

    public MaterialBauxite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(30,50,1,-64,180,1470,0.8,false,Optional.of(Biomes.IS_WET), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff999FAF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Aluminum, MetalEnum.Iron));
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());
        IGRecipeNode crushing =  IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100).addToTree(bayer_process);
        IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.POWDER).addOptionalToTree(bayer_process);
        IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT,
                ItemCategoryFlags.POWDER, 400, 32000).addToTree(bayer_process, crushing);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
               "ore_powder_" + getName() + "_to_slurry",
                ItemStack.EMPTY, //STACK
                new FluidStack(ChemicalEnum.SodiumHydroxide.getCloudySlurryWith(MineralEnum.Bauxite), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDER), 1),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).joinBranches(bayer_process, powder_a, powder_b);

        IGMethodBuilder.centrifuge(this, IGStageDesignation.REFINEMENT).create(
                ChemicalEnum.SodiumHydroxide.getCloudySlurryTagWith(MineralEnum.Bauxite),
                IGLib.SLURRY_TO_CRYSTAL_MB, MetalEnum.Aluminum, ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT,
                ChemicalEnum.ChemicalWaste.getCloudySlurryWith(MineralEnum.Bauxite),
                IGLib.ACID_RECOVERED_FROM_SLURRY, null, 0, 1200, 614400)
                .addToTree(bayer_process);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                "waste_"+getName()+"_to_oxide",
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
                ChemicalEnum.ChemicalWaste.getFluidStack(IGLib.ACID_TO_SLURRY_AMOUNT),
                new IngredientWithSize(MetalEnum.Calcium.getItemTag(ItemCategoryFlags.COMPOUND_DUST), 1),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.SLURRY_TO_CRYSTAL_MB),
                new FluidTagInput(ChemicalEnum.ChemicalWaste.getCloudySlurryTagWith(MineralEnum.Bauxite),IGLib.SLURRY_TO_CRYSTAL_MB),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.SLURRY_TO_CRYSTAL_MB),
                200, 51200).addToTree(bayer_process);

    }
    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(bayer_process);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.20800781f;
    }
}
