package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.Set;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        //TODO Remove Generalization category flags and set them up for proper minerals.
        addFlags(MaterialFlags.HAS_SLURRY, ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER);
    }

    public Set<IGRecipeChain> getRecipeChains()
    {
        return directBlasting.getRootNodes().isEmpty() ? Set.of() : Set.of(directBlasting);
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());

        boolean f = false;
        if (hasFlag(ItemCategoryFlags.PELLET))
        {
            f = true;
            IGRecipeNode grit = IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100).addOptionalToTree(directBlasting);
            IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER).addOptionalToTree(directBlasting);
            IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000).addToTree(directBlasting, grit);

            IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.POWDER).
                    joinBranches(directBlasting, powder_a, powder_b);

            IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                    getItemTag(ItemCategoryFlags.PELLET),
                    getPrimaryProduct().getStack(ItemCategoryFlags.INGOT)).addToTree(directBlasting);
        }

        if(hasFlag(ItemCategoryFlags.DIRTY_CRUSHED_ORE) && hasFlag(ItemCategoryFlags.CRUSHED_ORE))
        {
            IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, new ItemStack(Blocks.GRAVEL), 0.33f, 100, 100);
            if(!f && hasFlag(ItemCategoryFlags.GRIT)) IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100);
        }
    }

    public MineralConfig CONFIG = new MineralConfig(8,50,1,-48,112,50, 0.5,false,Optional.empty(), IGGenerationType.DEFAULT);
    public record MineralConfig(int veinSize, int rarity, int veinsPerChunk, int minY, int maxY, int generationChance, double density, boolean useSparsePlacement, Optional<TagKey<Biome>> preferredBiome, IGGenerationType generationType)
    {
    }

    @Override
    public IGVeinTextureType getVeinTextureType()
    {
        return IGVeinTextureType.MINERAL;
    }
}
