package com.igteam.immersivegeology.common.block.multiblock.gravityseparator;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.common.block.multiblock.IGClientMultiblockProperties;
import com.igteam.immersivegeology.common.block.multiblock.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class GravitySeparatorMultiblock extends IGTemplateMultiblock {
    public static final GravitySeparatorMultiblock INSTANCE = new GravitySeparatorMultiblock();

    private GravitySeparatorMultiblock(){
        super(new ResourceLocation(IGLib.MODID, "multiblocks/gravityseparator"),
                new BlockPos(1,0,1),
                new BlockPos(1,6,2),
                new BlockPos(3,7,3),
                IGMultiblockHolder.GRAVITY_SEPARATOR);
    }

    @Override
    public float getManualScale(){
        return 16;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 0, 0, 0));
    }
}
