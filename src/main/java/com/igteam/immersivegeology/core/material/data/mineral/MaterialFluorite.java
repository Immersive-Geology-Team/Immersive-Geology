package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialFluorite extends MaterialMineral {

    public MaterialFluorite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(14,60,2,10,120,1000, 0.5,true,Optional.of(Biomes.IS_WET), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x329870));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(ChemicalEnum.HydrofluoricAcid));
    }
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"solution_to_acid",
                MetalEnum.Calcium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                ChemicalEnum.HydrofluoricAcid.getFluidStack(125),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDER, 1)),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 125),
                null, null,
                200, 51200);
    }
}
