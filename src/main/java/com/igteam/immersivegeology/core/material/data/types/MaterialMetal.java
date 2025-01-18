/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class MaterialMetal extends GeologyMaterial {

    public MaterialMetal(){
        super();
        addFlags(BlockCategoryFlags.SLAB, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST, MaterialFlags.IS_MOLTEN_METAL, MaterialFlags.HAS_SLURRY);
    }

    @Override
    public void setupRecipeStages()
    {
        if (hasFlag(ItemCategoryFlags.OXIDE_PELLET))
        {
            IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.OXIDE_PELLET);
            IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                    getItemTag(ItemCategoryFlags.OXIDE_PELLET),
                    getProductionMaterial().getStack(ItemCategoryFlags.INGOT));

        }
    }

    @Override
    public MaterialInterface<?> getProductionMaterial()
    {
        return MetalEnum.valueOf(getName());
    }

    @Override
    public CrystalFamily getCrystalFamily()
    {
        return super.getCrystalFamily();
    }

    public MaterialMineral.MineralConfig CONFIG = new MaterialMineral.MineralConfig(0,50,0,-48,112,0,0.5, false, Optional.empty());
    public record MineralConfig(int veinSize, int rarity, int veinsPerChunk, int minY, int maxY, int generationChance, Optional<TagKey<Biome>> preferredBiome)
    {}
}
