package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        //TODO Remove Generalization category flags and set them up for proper minerals.
        addFlags(MaterialFlags.HAS_SLURRY, ItemCategoryFlags.SLAG, ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, ItemCategoryFlags.POWDERED_SLAG);
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());
        if (hasFlag(ItemCategoryFlags.PELLET))
        {
            IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.GRIT, 6000, 100);
            IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000);
            IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER);
            IGMethodBuilder.pelletize(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.POWDER);
            IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("pellet_"+getName()+"_to_ingot",
                    getItemTag(ItemCategoryFlags.PELLET),
                    getProductionMaterial().getStack(ItemCategoryFlags.INGOT));
        }
    }

    public MineralConfig CONFIG = new MineralConfig(8,50,1,-48,112,50, 0.5,false,Optional.empty());
    public record MineralConfig(int veinSize, int rarity, int veinsPerChunk, int minY, int maxY, int generationChance, double density, boolean useSparsePlacement, Optional<TagKey<Biome>> preferredBiome)
    {}

    @Override
    public IGVeinTextureType getVeinType()
    {
        return IGVeinTextureType.MINERAL;
    }
}
