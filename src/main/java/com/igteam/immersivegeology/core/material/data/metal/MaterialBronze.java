/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.metal;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import com.igteam.immersivegeology.common.block.multiblocks.IGBloomeryMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGReverberationFurnaceMultiblock;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetalAlloy;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;

import java.util.function.BiFunction;

public class MaterialBronze extends MaterialMetalAlloy
{
    public MaterialBronze() {
        super();
        addFlags(BlockCategoryFlags.FENCE);
        validMultiblocks.add(() -> IEMultiblocks.ADVANCED_BLAST_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.SILO);
        validMultiblocks.add(() -> IEMultiblocks.SHEETMETAL_TANK);
        validMultiblocks.add(() -> IEMultiblocks.ALLOY_SMELTER);
        validMultiblocks.add(() -> IEMultiblocks.COKE_OVEN);
        validMultiblocks.add(() -> IEMultiblocks.BLAST_FURNACE);
        validMultiblocks.add(() -> IEMultiblocks.FEEDTHROUGH);
        validMultiblocks.add(() -> IGReverberationFurnaceMultiblock.INSTANCE);
        validMultiblocks.add(() -> IGBloomeryMultiblock.INSTANCE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xffd0d5db));
    }
}
