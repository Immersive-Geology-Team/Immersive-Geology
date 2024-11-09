package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Blocks;

import java.util.List;
import java.util.function.Function;

public class MaterialVanilla extends MaterialStone {

    public MaterialVanilla() {
        super();
        this.STONE_FORMATION = StoneFormation.MINECRAFT_STONE; // IGNEOUS_EXTRUSIVE and Sedimentary
        this.name = "stone"; // Special Case as we need to override the default name assignment method
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public List<TargetBlockState> getTargets(MineralEnum mineral)
    {
        BlockState poor = mineral.getOreBlock(this, OreRichness.POOR).defaultBlockState();
        BlockState normal = mineral.getOreBlock(this, OreRichness.NORMAL).defaultBlockState();
        BlockState rich = mineral.getOreBlock(this, OreRichness.RICH).defaultBlockState();
        return List.of(OreConfiguration.target(new TagMatchTest(Blocks.STONE), normal));
    }
}
