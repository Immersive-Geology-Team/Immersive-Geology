package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialMagnetite extends MaterialMineral {

    public MaterialMagnetite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ItemCategoryFlags.PELLET);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        CONFIG = new MineralConfig(43,35,1,-64,320,1150, 0.60,false,Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.BANDED);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0x2A2A2A),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x9C5A33)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Iron, MetalEnum.Nickel));
    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 4,
                ItemCategoryFlags.INGOT, 1, 1200);

        // Straight up
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
               getPrimaryProduct().getStack(ItemCategoryFlags.INGOT), 900);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(getItemTag(ItemCategoryFlags.POWDER),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Nickel.getStack(ItemCategoryFlags.METAL_OXIDE), 0.075f, 200, 250);
        //TODO Think about byproducts, MAYBE add grav separation after pulverization to get 7.5% of nickel/chrome oxide ?
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.97540283f;
    }
}
