package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGRotaryKilnMultiblock extends IGTemplateMultiblock {

    public IGRotaryKilnMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/rotarykiln"), new BlockPos(0,0,0), new BlockPos(1,2,7), new BlockPos(3,3, 8), IGMultiblockProvider.ROTARYKILN);
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
