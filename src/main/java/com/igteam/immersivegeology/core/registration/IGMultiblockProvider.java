package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.*;

public class IGMultiblockProvider {
    public static final MultiblockRegistration<CrystallizerLogic.State> CRYSTALLIZER = IGRegistrationHolder.registerMetalMultiblock("crystallizer", new CrystallizerLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("crystallizer"),
            builder -> {
                builder.redstone(state -> state.rsState, CrystallizerLogic.REDSTONE_IN).notMirrored();
            });

    public static final MultiblockRegistration<GravitySeparatorLogic.State> GRAVITY_SEPARATOR = IGRegistrationHolder.registerMetalMultiblock("gravityseparator", new GravitySeparatorLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("gravityseparator"),
            builder -> {
                builder.redstone(state -> state.rsState, GravitySeparatorLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<RotaryKilnLogic.State> ROTARYKILN = IGRegistrationHolder.registerMetalMultiblock("rotarykiln", new RotaryKilnLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("rotarykiln"),
            builder -> {
                builder.redstone(state -> state.rsState, RotaryKilnLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<CoreDrillLogic.State> COREDRILL = IGRegistrationHolder.registerMetalMultiblock("coredrill", new CoreDrillLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("coredrill"),
            builder -> {
                builder.redstone(state -> state.rsState, CoreDrillLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<RevFurnaceLogic.State> REVERBERATION_FURNACE = IGRegistrationHolder.registerMetalMultiblock("reverberation_furnace", new RevFurnaceLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("reverberation_furnace"),
            builder -> {
                builder.redstone(state -> state.rsState, RevFurnaceLogic.REDSTONE_IN);
            });
    public static final MultiblockRegistration<IndSluiceLogic.State> INDUSTRIAL_SLUICE = IGRegistrationHolder.registerMetalMultiblock("industrial_sluice", new IndSluiceLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("industrial_sluice"),
            builder -> {
                builder.redstone(state -> state.rsState, IndSluiceLogic.REDSTONE_IN);
            });


    public static void forceClassLoad(){};
}
