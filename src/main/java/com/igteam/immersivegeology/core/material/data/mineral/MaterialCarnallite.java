package com.igteam.immersivegeology.core.material.data.mineral;
            
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.register.IEItems;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialEvaporateMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialCarnallite extends MaterialEvaporateMineral
{

    public MaterialCarnallite() {
        super();

        CONFIG = new MineralConfig(12,45,1,24,128,1350,0.5,false,Optional.of(Biomes.IS_SANDY), IGGenerationType.EVAPORATE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xFEDD9E));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Magnesium));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"solution_to_acid",
                new ItemStack(IEItems.Misc.FERTILIZER.get()),
                ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Magnesium, 125),
                IngredientWithSize.of(ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.Brine.getSlurryTagWith(BlockCategoryFlags.SLURRY, this), 125),
                new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                null,
                200, 51200);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"solution_to_acid",
                new ItemStack(IEItems.Misc.FERTILIZER.get()),
                ChemicalEnum.HydrochloricAcid.getFluidStack(125),
                IngredientWithSize.of(ItemStack.EMPTY),
                new FluidTagInput(ChemicalEnum.Brine.getSlurryTagWith(BlockCategoryFlags.SLURRY, this), 125),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                null,
                200, 51200);
    }

}
