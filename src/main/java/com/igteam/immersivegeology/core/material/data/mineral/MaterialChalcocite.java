package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.BiomeTags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChalcocite extends MaterialSulphideMineral
{

    public MaterialChalcocite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.PELLET);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

        setAsocialMaterialChance(1f);
        addGenerationFriend((h) -> MineralEnum.Chalcopyrite.instance(), 50);
        CONFIG = new MineralConfig(24,40,1,-32,200,400, 0.75,false, Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0x4A4A4A),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x2D7C6E)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        //MetalEnum.Platinum, MetalEnum.Osmium -- Think about it?
        return new LinkedHashSet<>(List.of(MetalEnum.Copper, MetalEnum.Nickel, MetalEnum.Platinum, MetalEnum.Osmium));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.roast(this, IGStageDesignation.ROASTING).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,   // Input
                ItemCategoryFlags.SLAG, 1,         // Output
                1000,                                          // Roasting Time
                200                                            // Sulfur Dioxide Output Amount
        ).addToTree(sulphideElectrowining);

        IGRecipeNode powdered_slag = IGMethodBuilder.pulverization(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG).addToTree(sulphideElectrowining);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                getPrimaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
                getSecondaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
                0.075f, 200, 1000).addToTree(sulphideElectrowining, powdered_slag);

        IGRecipeNode slurry = IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                ItemCategoryFlags.POWDERED_SLAG, BlockCategoryFlags.SLURRY,
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MineralEnum.Chalcocite, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDERED_SLAG, 3)),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).addToTree(sulphideElectrowining, powdered_slag);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                "mineral_slurry_"+getName() +"_to_" + getSecondaryProduct().getName() + "_crystal",
                MetalEnum.Copper.getStack(ItemCategoryFlags.CRYSTAL, IGLib.COMPOUND_FROM_ACID_AMOUNT),
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Nickel, IGLib.ACID_RECOVERED_FROM_SLURRY),
                ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MineralEnum.Chalcocite), IGLib.SLURRY_TO_CRYSTAL_MB,
                300, 38400).addToTree(sulphideElectrowining, slurry);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(directBlasting, sulphideElectrowining);
    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }
}
