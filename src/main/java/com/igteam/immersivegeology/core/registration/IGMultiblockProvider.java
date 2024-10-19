package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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

    public static final MultiblockRegistration<RevFurnaceLogic.State> REVERBERATION_FURNACE = IGRegistrationHolder.registerMultiblock("reverberation_furnace", new RevFurnaceLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("reverberation_furnace"), builder -> {}, Properties.copy(Blocks.STONE));

    public static final MultiblockRegistration<IndSluiceLogic.State> INDUSTRIAL_SLUICE = IGRegistrationHolder.registerMetalMultiblock("industrial_sluice", new IndSluiceLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("industrial_sluice"),
            builder -> {
                builder.redstone(state -> state.rsState, IndSluiceLogic.REDSTONE_IN);
            });
    public static final MultiblockRegistration<BloomeryLogic.State> BLOOMERY = IGRegistrationHolder.registerMultiblock("bloomery", new BloomeryLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("bloomery"), builder -> {}, Properties.copy(Blocks.STONE));

    public static final MultiblockRegistration<ChemicalReactorLogic.State> CHEMICAL_REACTOR = IGRegistrationHolder.registerMetalMultiblock("chemical_reactor", new ChemicalReactorLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("chemical_reactor"),
            builder -> {
                builder.redstone(state -> state.rsState, ChemicalReactorLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<CentrifugeLogic.State> CENTRIFUGE = IGRegistrationHolder.registerMetalMultiblock("centrifuge", new CentrifugeLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("centrifuge"),
            builder -> {
                builder.redstone(state -> state.rsState, CentrifugeLogic.REDSTONE_IN).notMirrored();
            });

    public static void forceClassLoad(){};
}
