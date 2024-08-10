package com.igteam.immersivegeology.common.block.multiblock.coredrill;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.common.block.multiblock.IGClientMultiblockProperties;
import com.igteam.immersivegeology.common.block.multiblock.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class CoreDrillMultiblock extends IGTemplateMultiblock {
    public static final CoreDrillMultiblock INSTANCE = new CoreDrillMultiblock();

    private CoreDrillMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/coredrill"),
                new BlockPos(7,7,7),
                new BlockPos(1,1,7),
                new BlockPos(9,12,9),
                IGMultiblockHolder.COREDRILL);
    }

    @Override
    public float getManualScale(){
        return 3;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 0, 0, 0));
    }
}
