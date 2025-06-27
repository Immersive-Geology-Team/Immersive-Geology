/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class MaterialMetal extends GeologyMaterial {

    public MaterialMetal(){
        super();
        addFlags(BlockCategoryFlags.SLAB, ItemCategoryFlags.ROD, ItemCategoryFlags.WIRE, ItemCategoryFlags.GRIT, ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.COMPOUND_DUST, MaterialFlags.IS_MOLTEN_METAL, MaterialFlags.HAS_SLURRY);
    }

    @Override
    public MaterialInterface<?> getPrimaryProduct()
    {
        return MetalEnum.valueOf(this.unserialized_name);
    }
    @Override
    public void setupRecipeStages()
    {
        if(hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE) && hasFlag(ItemCategoryFlags.CRUSHED_ORE))
        {
            IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, new ItemStack(Blocks.GRAVEL), 0.33f, 100, 100);
            if(hasFlag(ItemCategoryFlags.GRIT)) IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100);
        }

        if (hasFlag(ItemCategoryFlags.OXIDE_PELLET))
        {
            IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.OXIDE_PELLET);
            IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                    getItemTag(ItemCategoryFlags.OXIDE_PELLET),
                    getPrimaryProduct().getStack(ItemCategoryFlags.INGOT));
        }

        if (hasFlag(ItemCategoryFlags.CRYSTAL) && hasFlag(ItemCategoryFlags.INGOT))
        {
            IGMethodBuilder.arcSmelting(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRYSTAL,
                    1, ItemCategoryFlags.INGOT, 1, 0);
        }

        if (hasFlag(ItemCategoryFlags.GRIT) && hasFlag(ItemCategoryFlags.INGOT))
        {
            IGMethodBuilder.arcSmelting(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.GRIT,
                    1, ItemCategoryFlags.INGOT, 1, 0);
            IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.INGOT, ItemCategoryFlags.GRIT, 3000, 200);
        }

        if (hasFlag(ItemCategoryFlags.GRIT) && hasFlag(ItemCategoryFlags.CRYSTAL))
        {
            IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create(getName() + "_crystal_to_grit", getStack(ItemCategoryFlags.CRYSTAL, 1), getStack(ItemCategoryFlags.GRIT, 1), 3000, 200);
        }

    }

    @Override
    public CrystalFamily getCrystalFamily()
    {
        return super.getCrystalFamily();
    }

    public MaterialMineral.MineralConfig CONFIG = new MaterialMineral.MineralConfig(0,50,0,-48,112,0,0.5, false, Optional.empty(), IGGenerationType.DEFAULT);
    public record MineralConfig(int veinSize, int rarity, int veinsPerChunk, int minY, int maxY, int generationChance, Optional<TagKey<Biome>> preferredBiome)
    {}
}
