package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGArcSmeltingMethod;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        addFlags(MaterialFlags.HAS_SLURRY, ItemCategoryFlags.SLAG);
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());
    }
    public MineralConfig CONFIG = new MineralConfig(8,50,1,-48,112,50, false,Optional.empty());
    public record MineralConfig(int veinSize, int rarity, int veinsPerChunk, int minY, int maxY, int generationChance, boolean useSparsePlacement, Optional<TagKey<Biome>> preferredBiome)
    {}
}
