package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IETags;
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
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialUraninite extends MaterialMineral {

    public MaterialUraninite() {
        super();
        // in TFC is called 'PitchBlende'
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);

        CONFIG = new MineralConfig(33,80,1,-64,32,800, 0.5,true,Optional.of(Biomes.IS_COLD), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffB2BEB5));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Uranium, MetalEnum.Thorium));
    }
    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.POWDER);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT,
                ItemCategoryFlags.POWDER, 400, 32000);


        //TPB Solution of U ore
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"ore_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.PhosphoricAcid.getCloudySlurryWith(MineralEnum.Uraninite, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDER, 1)),
                new FluidTagInput(ChemicalEnum.PhosphoricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(IETags.fluidEthanol, IGLib.ACID_TO_SLURRY_AMOUNT),
                200, 51200);

        //Separation of U and Th
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"ore_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.NitricAcid.getCloudySlurryWith(MineralEnum.Uraninite, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.PhosphoricAcid.getCloudySlurryTagWith(MineralEnum.Uraninite), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new FluidTagInput(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                200, 51200);

        IGMethodBuilder.centrifuge(this, IGStageDesignation.REFINEMENT).create(
                        ChemicalEnum.NitricAcid.getCloudySlurryTagWith(MineralEnum.Uraninite),
                        IGLib.SLURRY_TO_CRYSTAL_MB, MetalEnum.Uranium, ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT,
                        ChemicalEnum.ChemicalWaste.getCloudySlurryWith(MineralEnum.Uraninite), IGLib.ACID_RECOVERED_FROM_SLURRY,
                ChemicalEnum.NitricAcid.getSlurryWith(MetalEnum.Thorium), IGLib.ACID_RECOVERED_FROM_SLURRY, 1200, 614400);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.15179443f;
    }
}
