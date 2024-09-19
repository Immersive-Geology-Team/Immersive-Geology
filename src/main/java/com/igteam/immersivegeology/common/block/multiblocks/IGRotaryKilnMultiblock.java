package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGRotaryKilnMultiblock extends IGTemplateMultiblock {

    public static final IGRotaryKilnMultiblock INSTANCE = new IGRotaryKilnMultiblock();

    public IGRotaryKilnMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/rotarykiln"), new BlockPos(0,0,0), new BlockPos(2,1,2), new BlockPos(8,3, 3), IGMultiblockProvider.ROTARYKILN);
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
