package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGCrystalizerMultiblock extends IGTemplateMultiblock {
    public static final IGCrystalizerMultiblock INSTANCE = new IGCrystalizerMultiblock();
    private IGCrystalizerMultiblock() {
        super(ResourceUtils.ig("multiblocks/crystallizer"), new BlockPos(0,0,0), new BlockPos(1,2,1), new BlockPos(3,3,3), IGMultiblockProvider.CRYSTALLIZER);
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
