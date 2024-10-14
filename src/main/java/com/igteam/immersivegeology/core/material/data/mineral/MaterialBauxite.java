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
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialBauxite extends MaterialMineral {

    public MaterialBauxite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x999FAF));
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
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());

        IGMethodBuilder.decompose(this, IGStageDesignation.EXTRACTION).create(
                "crushed_ore_" + getName() + "_to_oxide",
                MetalEnum.Aluminum.getStack(ItemCategoryFlags.METAL_OXIDE),
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1, 300, 153600);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("metal_oxide_" + getName() + "_to_compound_dust",
                MetalEnum.Aluminum.getStack(ItemCategoryFlags.COMPOUND_DUST, IGLib.COMPOUND_FROM_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0), new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.METAL_OXIDE)), new FluidTagInput(ChemicalEnum.SodiumHydroxide.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT), null, null, 200, 51200);
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("compound_dust_" + MetalEnum.Aluminum.getName() + "_to_cryolite",
                MineralEnum.Cryolite.getStack(ItemCategoryFlags.DUST, IGLib.DUST_FROM_COMPOUND_ACID_AMOUNT), new FluidStack(Fluids.EMPTY, 0), new IngredientWithSize(MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.COMPOUND_DUST), IGLib.COMPOUND_ACID_TO_DUST_AMOUNT), new FluidTagInput(ChemicalEnum.HydrofluoricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_DUST_AMOUNT), null, null, 200, 51200);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.PURIFICATION).create(
                        "aluminium_oxide_to_ingot",
                        MetalEnum.Aluminum.getItemTag(ItemCategoryFlags.METAL_OXIDE), 1,
                        MetalEnum.Aluminum.getStack(ItemCategoryFlags.INGOT),
                        MineralEnum.Cryolite.getStack(ItemCategoryFlags.DUST),
                        0.25f,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MineralEnum.Cryolite.getItemTag(ItemCategoryFlags.DUST), 1))
                .setTimeAndEnergy(200, 10240);

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }
}
