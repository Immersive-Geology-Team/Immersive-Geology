/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGCentrifugeMultiblock extends IGTemplateMultiblock
{

	public static final IGCentrifugeMultiblock INSTANCE = new IGCentrifugeMultiblock();
	public IGCentrifugeMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/centrifuge"), new BlockPos(1,1,1), new BlockPos(2,0,2), new BlockPos(3,4,3), IGMultiblockProvider.CENTRIFUGE);
    }


    @Override
    public float getManualScale() {
        return 12.0f;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new CentrifugeClientData(INSTANCE, 0.5, 0.5, 0.5));
    }

    public static class CentrifugeClientData extends IGClientMultiblockProperties {
        public CentrifugeClientData(TemplateMultiblock multiblock, double offX, double offY, double offZ){
            super(multiblock, offX, offY, offZ);
        }
	}
}
