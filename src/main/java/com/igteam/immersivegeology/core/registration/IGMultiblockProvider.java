package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGCrystalizerMultiblock;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.blocks.multiblocks.logic.CrystallizerLogic;
import com.igteam.immersivegeology.common.blocks.multiblocks.logic.GravitySeparatorLogic;
import com.igteam.immersivegeology.common.blocks.multiblocks.logic.RotaryKilnLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;

public class IGMultiblockProvider {
    public static final MultiblockRegistration<CrystallizerLogic.State> CRYSTALLIZER = IGRegistrationHolder.registerMetalMultiblock("crystallizer", new CrystallizerLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("crystallizer"),
            builder -> {
                builder.redstone(state -> state.rsState, CrystallizerLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<GravitySeparatorLogic.State> GRAVITY_SEPARATOR = IGRegistrationHolder.registerMetalMultiblock("gravityseparator", new GravitySeparatorLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("gravityseparator"),
            builder -> {
                builder.redstone(state -> state.rsState, GravitySeparatorLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<RotaryKilnLogic.State> ROTARYKILN = IGRegistrationHolder.registerMetalMultiblock("rotarykiln", new RotaryKilnLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("rotarykiln"),
            builder -> {
                builder.redstone(state -> state.rsState, RotaryKilnLogic.REDSTONE_IN);
            });

    public static void forceClassLoad(){};
}
