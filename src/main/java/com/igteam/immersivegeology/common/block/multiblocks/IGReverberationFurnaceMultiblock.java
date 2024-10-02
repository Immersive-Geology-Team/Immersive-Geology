/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGReverberationFurnaceMultiblock extends IGTemplateMultiblock {

    public static final IGReverberationFurnaceMultiblock INSTANCE = new IGReverberationFurnaceMultiblock();

    public IGReverberationFurnaceMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/reverberation_furnace"), new BlockPos(0,0,0), new BlockPos(1,1,5), new BlockPos(6,12,6), IGMultiblockProvider.REVERBERATION_FURNACE);

    }

    @Override
    public float getManualScale() {
        return 8;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 0.5, 0.5, 0.5));
    }
}
