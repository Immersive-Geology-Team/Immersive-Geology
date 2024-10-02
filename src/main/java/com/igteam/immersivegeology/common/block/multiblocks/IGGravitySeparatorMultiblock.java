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

public class IGGravitySeparatorMultiblock extends IGTemplateMultiblock {

    public static final IGGravitySeparatorMultiblock INSTANCE = new IGGravitySeparatorMultiblock();

    public IGGravitySeparatorMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/gravityseparator"), new BlockPos(1,0,1), new BlockPos(1,6,2), new BlockPos(3,7,3), IGMultiblockProvider.GRAVITY_SEPARATOR);
    }

    @Override
    public float getManualScale() {
        return 8;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 1.5, 0.5, 1.5));
    }
}
