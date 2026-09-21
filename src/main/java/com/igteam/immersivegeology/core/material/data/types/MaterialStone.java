package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.Blocks;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MaterialStone extends GeologyMaterial {

    protected StoneFormation STONE_FORMATION = StoneFormation.IGNEOUS_INTRUSIVE;

    /**
     * The dimensions this stone makes can spawn in. Overworld unless a subclass says otherwise.
     */
    protected Set<ResourceLocation> DIMENSIONS = Set.of(World.OVERWORLD.location());

    public MaterialStone() {
        super();
        addFlags(MaterialFlags.IS_ORE_BEARING);
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        if(flag instanceof BlockCategoryFlags) return new ResourceLocation("minecraft", "block/"+getName());
        return super.getTextureLocation(flag);
    }

    public StoneFormation getStoneFormation()
    {
        return this.STONE_FORMATION;
    }

    public Set<ResourceLocation> getDimensions()
    {
        return this.DIMENSIONS;
    }

    @Override
    public void setupRecipeStages()
    {

    }

    public IGBlockProperties getProperties(IFlagType<?> flag)
    {
        return IGLib.STONE_DECO_PROPS;
    }

    public List<TargetBlockState> getTargets(MineralEnum mineral)
    {
        return List.of();
    }

    public String getTFCStoneLoc()
    {
        return "tfc:rock/raw/" + getName().toLowerCase(Locale.ROOT);
    }
}
