package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialHematite extends MaterialMineral {

    public MaterialHematite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addFlags(ItemCategoryFlags.PELLET);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        CONFIG = new MineralConfig(32,50,2,-64,256,2000, 0.66,false,Optional.of(Biomes.IS_MOUNTAIN), IGGenerationType.BANDED);
    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff4B2F2C));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Iron,MetalEnum.Chromium));
    }

    @Override
    public IGVeinTextureType getVeinTextureType()
    {
        return IGVeinTextureType.LAYERED;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(
                ItemCategoryFlags.CRUSHED_ORE, 4,
                ItemCategoryFlags.INGOT, 1, 1100);

        // Straight up
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT), 900);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(getItemTag(ItemCategoryFlags.POWDER),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Chromium.getStack(ItemCategoryFlags.METAL_OXIDE), 0.075f, 200, 1000);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.96429443f;
    }
}
