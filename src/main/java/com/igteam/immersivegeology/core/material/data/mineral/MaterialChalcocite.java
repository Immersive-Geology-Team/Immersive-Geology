package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialChalcocite extends MaterialMineral {

    public MaterialChalcocite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x3D5E67));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Copper, MetalEnum.Platinum, MetalEnum.Osmium));
    }

    @Override
    public void setupRecipeStages()
    {
        IGMethodBuilder.roast(this, IGStageDesignation.ROASTING).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,   // Input
                ItemCategoryFlags.SLAG, 1,         // Output
                1000,                                          // Roasting Time
                200                                            // Sulfur Dioxide Output Amount
        );

        IGMethodBuilder.blasting(this, IGStageDesignation.BLASTING).create(
                "slag_" + getName() + "_to_metal",
                getItemTag(ItemCategoryFlags.SLAG),
                MetalEnum.Copper.getStack(ItemCategoryFlags.INGOT)
        );

        IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create(
                "slag_to_dust",
                getItemTag(ItemCategoryFlags.SLAG),
                getStack(ItemCategoryFlags.DUST, 1),
                3000,
                200
        );

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("dust_" + getName() + "_to_slurry",
                ItemStack.EMPTY,
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Copper, IGLib.SLURRY_FROM_ACID_AMOUNT),
                new IngredientWithSize(getItemTag(ItemCategoryFlags.DUST), IGLib.DUST_TO_SLURRY_AMOUNT),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(), IGLib.ACID_TO_SLURRY_AMOUNT),
                null,
                null,
                200,
                51200);

        IGMethodBuilder.crystallize(this, IGStageDesignation.PURIFICATION).create("crystallize_copper_slurry",
                MetalEnum.Copper.getStack(ItemCategoryFlags.CRYSTAL),
                ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MetalEnum.Copper),
                IGLib.SLURRY_TO_CRYSTAL_MB,
                300, 38400);
    }
}
