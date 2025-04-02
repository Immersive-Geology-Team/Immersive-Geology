package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialCassiterite extends MaterialMineral {

    public MaterialCassiterite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ItemCategoryFlags.PELLET);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        CONFIG = new MineralConfig(40,50,1,0,175,2590, 0.5,false,Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x5A4D4D));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Tin, MetalEnum.Tungsten));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRUSHED_ORE,
                2, ItemCategoryFlags.INGOT, 1, 400);

        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                MetalEnum.Tin.getStack(ItemCategoryFlags.INGOT), 900);


        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(getItemTag(ItemCategoryFlags.POWDER),
                MetalEnum.Tin.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Tungsten.getStack(ItemCategoryFlags.METAL_OXIDE), 0.075f, 200, 1000);

    }

    @Override
    public float getNoiseProbability()
    {
        return 0.20593262f;
    }
}
