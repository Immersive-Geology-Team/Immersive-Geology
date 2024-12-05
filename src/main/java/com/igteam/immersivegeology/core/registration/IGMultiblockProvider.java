package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.*;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockBuilder;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

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

    public static final MultiblockRegistration<RevFurnaceLogic.State> REVERBERATION_FURNACE = mirroredStone(new RevFurnaceLogic(), "reverberation_furnace", false)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("reverberation_furnace"))
            .gui(IGMenuTypes.REVERBERATION_FURNACE)
            .build();

    public static final MultiblockRegistration<TrommelLogic.State> TROMMEL = IGRegistrationHolder.registerMetalMultiblock("trommel", new TrommelLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("trommel"),
            builder -> {
                builder.redstone(state -> state.rsState, TrommelLogic.REDSTONE_IN);
            });
    public static final MultiblockRegistration<BloomeryLogic.State> BLOOMERY = stone(new BloomeryLogic(), "bloomery", false)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("bloomery"))
            .gui(IGMenuTypes.BLOOMERY)
            .build();

    public static final MultiblockRegistration<ChemicalReactorLogic.State> CHEMICAL_REACTOR = IGRegistrationHolder.registerMetalMultiblock("chemical_reactor", new ChemicalReactorLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("chemical_reactor"),
            builder -> {
                builder.redstone(state -> state.rsState, ChemicalReactorLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<CentrifugeLogic.State> CENTRIFUGE = IGRegistrationHolder.registerMetalMultiblock("centrifuge", new CentrifugeLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("centrifuge"),
            builder -> {
                builder.redstone(state -> state.rsState, CentrifugeLogic.REDSTONE_IN).notMirrored();
            });

    public static final MultiblockRegistration<BallmillLogic.State> BALLMILL = IGRegistrationHolder.registerMetalMultiblock("ballmill", new BallmillLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("ballmill"),
            builder -> {
                builder.redstone(state -> state.rsState, BallmillLogic.REDSTONE_IN).notMirrored();
            });

    public static final MultiblockRegistration<PelletizerLogic.State> PELLETIZER = IGRegistrationHolder.registerMetalMultiblock("pelletizer", new PelletizerLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("pelletizer"),
            builder -> {
                builder.redstone(state -> state.rsState, PelletizerLogic.REDSTONE_IN);
            });

    private static <S extends IMultiblockState> IGMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid)
    {
        Properties properties = Properties.of()
                .sound(SoundType.NETHER_BRICKS)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .forceSolidOn()
                .strength(2, 20);
        if(!solid)
            properties.noOcclusion();
        else
            properties.forceSolidOn();
        return new IGMultiblockBuilder<>(logic, name)
                .notMirrored()
                .customBlock(
                        IGRegistrationHolder.getBlockRegister(), IGRegistrationHolder.getItemRegister(),
                        r -> new NonMirrorableWithActiveBlock<>(properties, r),
                        MultiblockItem::new
                )
                .defaultBEs(IGRegistrationHolder.getTeRegister());
    }

    private static <S extends IMultiblockState> IGMultiblockBuilder<S> mirroredStone(IMultiblockLogic<S> logic, String name, boolean solid)
    {
        Properties properties = Properties.of()
                .sound(SoundType.NETHER_BRICKS)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .forceSolidOn()
                .strength(2, 20);
        if(!solid)
            properties.noOcclusion();
        else
            properties.forceSolidOn();
        return new IGMultiblockBuilder<>(logic, name)
                .defaultBEs(IGRegistrationHolder.getTeRegister())
                .defaultBlock(IGRegistrationHolder.getBlockRegister(), IGRegistrationHolder.getItemRegister(), properties);
    }

    public static void forceClassLoad(){};
}
