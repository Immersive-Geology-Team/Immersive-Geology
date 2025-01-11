package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialGalena extends MaterialMineral {

    public MaterialGalena() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);

        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        // TODO Goto Nether
        CONFIG = new MineralConfig(12,45,2,-64,60,750,0.5,false, Optional.of(Biomes.IS_COLD));
        this.addFlags(ItemCategoryFlags.POWDERED_SLAG);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x857F83));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Lead, MetalEnum.Silver));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        //IGMethodBuilder.roast(this, IGStageDesignation.ROASTING).create("dust_"+getName() + "");

        IGMethodBuilder.blasting(this, IGStageDesignation.BLASTING).create("pellet" + getName() + "_to_metal",
                getItemTag(ItemCategoryFlags.PELLET), getProductionMaterial().getStack(ItemCategoryFlags.INGOT));

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRUSHED_ORE, 2, ItemCategoryFlags.INGOT, 1, 400);

        //loss of SO2
        //loss of silver

        //roast it -> So2 + SLAG

        IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG), getProductionMaterial().getStack(ItemCategoryFlags.METAL_OXIDE),
                getByproductMaterial().getStack(ItemCategoryFlags.GRIT), 0.33f, 100, 100);

        //TODO arcSmelting with bones!
    }
}
