package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags.Blocks;

import java.util.List;
import java.util.function.BiFunction;

public class MaterialVanilla extends MaterialStone {

    public MaterialVanilla() {
        super();
        this.STONE_FORMATION = StoneFormation.MINECRAFT_STONE; // IGNEOUS and Sedimentary
        this.name = "stone"; // Special Case as we need to override the default name assignment method
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ModFlags.MINECRAFT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public List<TargetBlockState> getTargets(MineralEnum mineral)
    {
        BlockState poor = mineral.getOreBlock(this, OreRichness.POOR).getIGDefaultBlockState();
        BlockState normal = mineral.getOreBlock(this, OreRichness.NORMAL).getIGDefaultBlockState();
        BlockState rich = mineral.getOreBlock(this, OreRichness.RICH).getIGDefaultBlockState();
        return List.of(OreConfiguration.target(new TagMatchTest(Blocks.STONE), normal));
    }
}
