package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.function.BiFunction;

public class MaterialMCBasalt extends MaterialStone {

    public MaterialMCBasalt() {
        super();
        this.name = "basalt"; // Special Case as we need to override the default name assignment method
        this.STONE_FORMATION = StoneFormation.NETHER_STONE;
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ModFlags.MINECRAFT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public boolean useColumnBlockStyle(IFlagType<?> flag)
    {
        return true;
    }

    @Override
    public Properties getProperties(IFlagType<?> flag)
    {
        return BlockBehaviour.Properties.copy(Blocks.BASALT);
    }
}
