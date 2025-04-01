package com.igteam.immersivegeology.core.material.data.mineral;
            
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChromite extends MaterialMineral {

    public MaterialChromite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(30,45,1,-64,24,1350,0.5,false,Optional.of(Biomes.IS_COLD), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x615964));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Chromium, MetalEnum.Iron));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT,
                6000, 100);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER,
                200, 16000);
        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.POWDER);

        IGMethodBuilder.arcSmelting(this, IGStageDesignation.EXTRACTION).create(
                        "separate_oxides_from_"+getName(),
                        getItemTag(ItemCategoryFlags.POWDER), 1,
                        MetalEnum.Chromium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                        ItemStack.EMPTY,
                        new IngredientWithSize(IETags.coalCokeDust, 1),
                        new IngredientWithSize(MetalEnum.Sodium.getItemTag(ItemCategoryFlags.COMPOUND_DUST), 1))
                .addExtras(MetalEnum.Iron.getItemTag(ItemCategoryFlags.INGOT), 0.8f)
                .setTimeAndEnergy(400, 204800);




    }
}
