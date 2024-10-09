package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;
import java.util.function.Function;

public class MaterialVanadinite extends MaterialMineral {

    public MaterialVanadinite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xEF2161));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        IGLib.IG_LOGGER.info("Setting up Stages for Material {}", getName());

        new IGRecipeStage(this, IGStageDesignation.LEECHING)
        {
            @Override
            protected void describe()
            {
                // crushing
                // chemical
                IGMethodBuilder.decompose(this).create("compound_dust_"+ MetalEnum.Vanadium.getName() + "_to_metal_oxide",
                        MetalEnum.Vanadium.getStack(ItemCategoryFlags.METAL_OXIDE),
                        MetalEnum.Vanadium.getItemTag(ItemCategoryFlags.COMPOUND_DUST),
                        1,
                        300,
                        153600);

                IGMethodBuilder.chemical(this).create("dust_" + getName() + "_to_" + MetalEnum.Vanadium.getName() + "_" + ItemCategoryFlags.COMPOUND_DUST.getName(),
                        MetalEnum.Vanadium.getStack(ItemCategoryFlags.COMPOUND_DUST, 2),
                        new FluidStack(Fluids.WATER, 250),
                        IngredientWithSize.of(getStack(ItemCategoryFlags.DUST, 1)),
                        new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 250), new FluidTagInput(ChemicalEnum.Brine.getFluidTag(BlockCategoryFlags.FLUID), 250), null,
                        200, 51200);
            }
        };

        IGLib.IG_LOGGER.info("Final Stages for Material {}", getName());
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials()
    {
        return Set.of(MetalEnum.Lead, MetalEnum.Vanadium);
    }

}
