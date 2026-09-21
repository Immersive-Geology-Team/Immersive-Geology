package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.core.lib.shim.MCShims.OreConfiguration;
import com.igteam.immersivegeology.core.lib.shim.MCShims.Tags;

import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IMultiblock;

import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGBloomeryMultiblock;
import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGReverberationFurnaceMultiblock;

import com.igteam.immersivegeology.core.lib.shim.MCShims.TargetBlockState;
import com.igteam.immersivegeology.core.lib.shim.MCShims.TagMatchTest;

import net.minecraft.block.state.IBlockState;

import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IEPlaceholders.IEMultiblocks;

import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.init.Blocks;

import java.util.List;
import java.util.function.BiFunction;

public class MaterialVanilla extends MaterialStone {

    public MaterialVanilla() {
        super();
        this.STONE_FORMATION = StoneFormation.MINECRAFT_STONE; // IGNEOUS and Sedimentary
        this.name = "stone"; // Special Case as we need to override the default name assignment method
        addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ModFlags.MINECRAFT);
        validMultiblocks.add(() -> IEMultiblocks.ALLOY_SMELTER);
        validMultiblocks.add(() -> IEMultiblocks.COKE_OVEN);
        validMultiblocks.add(() -> IEMultiblocks.BLAST_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.FEEDTHROUGH);
        validMultiblocks.add(() -> IGBloomeryMultiblock.INSTANCE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
    }

    @Override
    public List<TargetBlockState> getTargets(MineralEnum mineral)
    {
        IBlockState poor = mineral.getOreBlock(this, OreRichness.POOR).getIGDefaultBlockState();
        IBlockState normal = mineral.getOreBlock(this, OreRichness.NORMAL).getIGDefaultBlockState();
        IBlockState rich = mineral.getOreBlock(this, OreRichness.RICH).getIGDefaultBlockState();
        return List.of(OreConfiguration.target(new TagMatchTest(Tags.Blocks.STONE), normal));
    }

    @Override
    public boolean canFormMB(IMultiblock multiblock)
    {
        return super.canFormMB(multiblock);
    }

    @Override
    public IGBlockProperties getProperties(IFlagType<?> flag)
    {
        return IGBlockProperties.copy(Blocks.STONE);
    }
}
