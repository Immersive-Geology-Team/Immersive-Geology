package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.blocks.multiblocks.logic.CrystallizerLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class IGMultiblockProvider {
    public static final MultiblockRegistration<CrystallizerLogic.State> CRYSTALLIZER = IGRegistrationHolder.registerMetalMultiblock("crystallizer", new CrystallizerLogic(), () -> IGMultiblocks.CRYSTALLIZER,
            builder -> {
                builder.redstone(state -> state.rsState, CrystallizerLogic.REDSTONE_IN);
            });

    public static void forceClassLoad(){};
}
