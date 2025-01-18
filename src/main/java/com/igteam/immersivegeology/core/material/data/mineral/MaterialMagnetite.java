package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGPelletizerMethod;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        CONFIG = new MineralConfig(15,35,1,0,180,1000, 0.5,false,Optional.empty());
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
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Iron));
    }

    @Override
    public boolean willTarnishOverTime()
    {
        return true;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        // Straight up
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
               getProductionMaterial().getStack(ItemCategoryFlags.INGOT));

        //TODO Think about byproducts, MAYBE add grav separation after pulverization to get 7.5% of nickel/chrome oxide ?
    }
}
