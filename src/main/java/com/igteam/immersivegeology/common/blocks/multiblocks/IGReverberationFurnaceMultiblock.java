package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGReverberationFurnaceMultiblock extends IGTemplateMultiblock {

    public IGReverberationFurnaceMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/reverberation_furnace"), new BlockPos(0,0,0), new BlockPos(1,1,5), new BlockPos(6,12,6), IGMultiblockProvider.REVERBERATION_FURNACE);

    }

    @Override
    public float getManualScale() {
        return 9;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 2.5));
    }
}
