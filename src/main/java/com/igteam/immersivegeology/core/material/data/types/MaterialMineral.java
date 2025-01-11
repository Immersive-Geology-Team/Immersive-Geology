package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class MaterialMineral extends GeologyMaterial {

    public MaterialMineral(){
        super();
        addFlags(MaterialFlags.HAS_SLURRY, ItemCategoryFlags.SLAG, ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER);
    }

    @Override
    public void setupRecipeStages()
    {
        logged_recipes.add(getName());
        if (hasFlag(ItemCategoryFlags.PELLET))
        {
            //pelletizer recipe
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
