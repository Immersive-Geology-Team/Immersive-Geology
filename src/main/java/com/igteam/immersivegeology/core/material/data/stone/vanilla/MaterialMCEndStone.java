package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import net.minecraft.world.World;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.Set;
import net.minecraft.init.Blocks;

import java.util.function.BiFunction;

public class MaterialMCEndStone extends MaterialStone {

    public MaterialMCEndStone() {
        super();
        this.name = "end_stone"; // Special Case as we need to override the default name assignment method
        this.STONE_FORMATION = StoneFormation.END_STONE;
        this.DIMENSIONS = java.util.Collections.singleton(new net.minecraft.util.ResourceLocation("the_end"));
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ModFlags.MINECRAFT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }


    @Override
    public IGBlockProperties getProperties(IFlagType<?> flag)
    {
        return IGBlockProperties.copy(Blocks.END_STONE);
    }
}
