package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialBauxite extends MaterialMineral {

    public MaterialBauxite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(14,50,1,0,180,1200,0.5,false,Optional.empty());
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x999FAF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Aluminum));
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

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
               "ore_powder_" + getName() + "_to_slurry",
                ItemStack.EMPTY, //STACK
                new FluidStack(ChemicalEnum.SodiumHydroxide.getCloudySlurryWith(MineralEnum.Bauxite), IGLib.SLURRY_FROM_ACID_AMOUNT),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDER), 1),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200);

        IGMethodBuilder.centrifuge(this, IGStageDesignation.REFINEMENT).create(
                ChemicalEnum.SodiumHydroxide.getCloudySlurryTagWith(MineralEnum.Bauxite),
                IGLib.SLURRY_TO_CRYSTAL_MB, MetalEnum.Aluminum, ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT,
                ChemicalEnum.ChemicalWaste.getCloudySlurryWith(MineralEnum.Bauxite),
                IGLib.ACID_RECOVERED_FROM_SLURRY, null, 0, 1200, 614400);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("metal_oxide_" + getName() + "_to_compound_dust",
                MetalEnum.Aluminum.getStack(ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0),
                new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.METAL_OXIDE)),
                new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT),
                null, null, 200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("compound_dust_" + MetalEnum.Aluminum.getName() + "_to_cryolite",
                MineralEnum.Cryolite.getStack(ItemCategoryFlags.POWDER, IGLib.DUST_FROM_COMPOUND_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0),
                new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.COMPOUND_DUST), IGLib.COMPOUND_ACID_TO_DUST_AMOUNT),
                new FluidTagInput(ChemicalEnum.HydrofluoricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_DUST_AMOUNT),
                null, null, 200, 51200);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "aluminium_oxide_to_ingot",
                        MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.METAL_OXIDE), 1,
                        MetalEnum.Aluminum.getStack(ItemCategoryFlags.INGOT),
                        MineralEnum.Cryolite.getStack(ItemCategoryFlags.POWDER),
                        0.25f,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.POWDER), 1))
                .setTimeAndEnergy(400, 204800);
    }
}
