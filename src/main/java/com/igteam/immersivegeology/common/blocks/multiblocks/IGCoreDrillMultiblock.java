package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGCoreDrillMultiblock extends IGTemplateMultiblock {

    public IGCoreDrillMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/coredrill"), new BlockPos(7,7,7), new BlockPos(1,1,7), new BlockPos(9,12,9), IGMultiblockProvider.COREDRILL);
    }

    @Override
    public float getManualScale() {
        return 6;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 2.5));
    }
}
