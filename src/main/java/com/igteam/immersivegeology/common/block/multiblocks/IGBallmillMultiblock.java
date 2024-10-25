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

public class IGBallmillMultiblock extends IGTemplateMultiblock {

    public static final IGBallmillMultiblock INSTANCE = new IGBallmillMultiblock();

    public IGBallmillMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/ballmill"), new BlockPos(2,1,1), new BlockPos(2,1,6), new BlockPos(5,6, 7), IGMultiblockProvider.BALLMILL);
    }

    @Override
    public float getManualScale() {
        return 8;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 6.5, 1.5, 1.5));
    }
}
