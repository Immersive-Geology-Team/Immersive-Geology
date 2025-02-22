package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialVanadinite extends MaterialSulphideMineral
{

    public MaterialVanadinite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);

        CONFIG = new MineralConfig(10,70,1,-32,140,600, 0.5,false,Optional.of(Biomes.IS_HOT), IGGenerationType.TUBE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xEF2161));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
//        IGMethodBuilder.decompose(this, IGStageDesignation.REFINEMENT).create("compound_dust_"+ MetalEnum.Vanadium.getName() + "_to_metal_oxide",
//                MetalEnum.Vanadium.getStack(ItemCategoryFlags.METAL_OXIDE),
//                MetalEnum.Vanadium.getItemTag(ItemCategoryFlags.COMPOUND_DUST),
//                1,
//                300,
//                153600);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(ItemCategoryFlags.POWDER, ItemCategoryFlags.COMPOUND_DUST,
                MetalEnum.Vanadium.getStack(ItemCategoryFlags.COMPOUND_DUST, 2),
                new FluidStack(Fluids.WATER, 250),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDER, 1)),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 250), new FluidTagInput(ChemicalEnum.Brine.getSlurryTagWith(MineralEnum.Rocksalt), 250), null,
                200, 51200);
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Vanadium, MetalEnum.Lead));
    }

}
