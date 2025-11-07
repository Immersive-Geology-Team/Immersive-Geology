package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.*;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGMultiblockBuilder;
import com.igteam.immersivegeology.common.block.multiblocks.part.*;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class IGMultiblockProvider {

    public static final ArrayList<MultiblockRegistration<?>> ALL_IG_MULTIBLOCKS = new ArrayList<>();

    public static final MultiblockRegistration<CrystallizerLogic.State> CRYSTALLIZER = metal_skinnable(new CrystallizerLogic(), "crystallizer", false, CrystallizerPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("crystallizer"))
            .gui(IGMenuTypes.CRYSTALLIZER)
            .redstone(state -> state.rsState, CrystallizerLogic.REDSTONE_IN)
            .build();

    public static final MultiblockRegistration<RotaryKilnLogic.State> ROTARYKILN = metal_skinnable(new RotaryKilnLogic(), "rotary_kiln", false, RotaryKilnPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("rotary_kiln"))
            .redstone(state -> state.rsState, RotaryKilnLogic.REDSTONE_IN)
            .gui(IGMenuTypes.ROTARY_KILN)
            .build();

    public static final MultiblockRegistration<FoundryLogic.State> FOUNDRY = IGRegistrationHolder.registerMetalMultiblock("foundry", new FoundryLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("foundry"),
            builder -> {
                builder.redstone(state -> state.rsState, FoundryLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<CoreDrillLogic.State> COREDRILL = IGRegistrationHolder.registerMetalMultiblock("coredrill", new CoreDrillLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("coredrill"),
            builder -> {
                builder.redstone(state -> state.rsState, CoreDrillLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<RevFurnaceLogic.State> REVERBERATION_FURNACE = stone_skinnable(new RevFurnaceLogic(),"reverberation_furnace", false, RevFurnacePart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("reverberation_furnace"))
            .gui(IGMenuTypes.REVERBERATION_FURNACE)
            .build();

    public static final MultiblockRegistration<BloomeryLogic.State> BLOOMERY = stone_skinnable(new BloomeryLogic(), "bloomery", false, BloomeryPart::new)
                    .structure(() -> IGRegistrationHolder.getMBTemplate.apply("bloomery"))
                    .gui(IGMenuTypes.BLOOMERY)
            .build();

    public static final MultiblockRegistration<GravitySeparatorLogic.State> GRAVITY_SEPARATOR = metal_skinnable(new GravitySeparatorLogic(), "gravity_separator", false, GravitySeparatorPart::new)
                    .structure(() -> IGRegistrationHolder.getMBTemplate.apply("gravity_separator"))
                    .redstone(state -> state.rsState, GravitySeparatorLogic.REDSTONE_IN)
                    .build();

    public static final MultiblockRegistration<ChemicalReactorLogic.State> CHEMICAL_REACTOR = metal_skinnable(new ChemicalReactorLogic(), "chemical_reactor", false, ChemicalReactorPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("chemical_reactor"))
            .gui(IGMenuTypes.CHEMICAL_REACTOR)
            .redstone(state -> state.rsState, ChemicalReactorLogic.REDSTONE_IN).build();

    public static final MultiblockRegistration<CentrifugeLogic.State> CENTRIFUGE = IGRegistrationHolder.registerMetalMultiblock("centrifuge", new CentrifugeLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("centrifuge"),
            builder -> {
                builder.redstone(state -> state.rsState, CentrifugeLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<BallmillLogic.State> BALLMILL = IGRegistrationHolder.registerMetalMultiblock("ballmill", new BallmillLogic(), () -> IGRegistrationHolder.getMBTemplate.apply("ballmill"),
            builder -> {
                builder.redstone(state -> state.rsState, BallmillLogic.REDSTONE_IN);
            });

    public static final MultiblockRegistration<PelletizerLogic.State> PELLETIZER = metal_skinnable(new PelletizerLogic(), "pelletizer", false, PelletizerPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("pelletizer"))
            .redstone(state -> state.rsState, PelletizerLogic.REDSTONE_IN)
            .build();

    public static final MultiblockRegistration<SteamTurbineLogic.State> STEAM_TURBINE = metal_skinnable(new SteamTurbineLogic(), "steam_turbine", false, SteamTurbinePart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("steam_turbine"))
            .redstone(state -> state.rsState, SteamTurbineLogic.REDSTONE_IN)
            .build();

    public static final MultiblockRegistration<GeothermalExchangerLogic.State> GEOTHERMAL_EXCHANGER = metal_skinnable(new GeothermalExchangerLogic(), "geothermal_exchanger", false, GeothermalPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("geothermal_exchanger"))
            .redstone(state -> state.rsState, GeothermalExchangerLogic.REDSTONE_IN)
            .gui(IGMenuTypes.GEOTHERMAL_EXCHANGER)
            .build();

    public static final MultiblockRegistration<SmallChemicalReactorLogic.State> SMALL_CHEMICAL_REACTOR = metal_skinnable(new SmallChemicalReactorLogic(), "small_chemical_reactor", false, SmallChemicalReactorPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("small_chemical_reactor"))
            .redstone(state -> state.rsState, SmallChemicalReactorLogic.REDSTONE_IN)
            .gui(IGMenuTypes.SMALL_CHEMICAL_REACTOR)
            .build();

    public static final MultiblockRegistration<AlternatorLogic.State> ALTERNATOR = metal_skinnable(new AlternatorLogic(), "alternator", false, AlternatorPart::new)
            .structure(() -> IGRegistrationHolder.getMBTemplate.apply("alternator"))
            .redstone(state -> state.rsState, AlternatorLogic.REDSTONE_IN)
            .build();

    private static <S extends IMultiblockState, B extends SkinableMultiblockPart<S, ?>>
    IGMultiblockBuilder<S> metal_skinnable(
            IMultiblockLogic<S> logic,
            String name,
            boolean solid,
            BiFunction<Properties, MultiblockRegistration<S>, B> blockCtor
    )
    {
        Properties props = Properties.of()
                .sound(SoundType.METAL)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2, 20);
        return skinnable(logic, name, solid, blockCtor, props);
    }

    private static <S extends IMultiblockState, B extends SkinableMultiblockPart<S, ?>>
    IGMultiblockBuilder<S> stone_skinnable(
            IMultiblockLogic<S> logic,
            String name,
            boolean solid,
            BiFunction<Properties, MultiblockRegistration<S>, B> blockCtor
    )
    {
        Properties props = Properties.of()
                .sound(SoundType.NETHER_BRICKS)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2, 20);
        return skinnable(logic, name, solid, blockCtor, props);
    }

    private static <S extends IMultiblockState, B extends SkinableMultiblockPart<S, ?>>
    IGMultiblockBuilder<S> skinnable(
            IMultiblockLogic<S> logic,
            String name,
            boolean solid,
            BiFunction<Properties, MultiblockRegistration<S>, B> blockCtor, Properties props
    )
    {

        if(!solid)
            props.noOcclusion();
        else
            props.forceSolidOn();

        return new IGMultiblockBuilder<>(logic, name)
                .customBlock(
                        IGRegistrationHolder.getBlockRegister(),
                        IGRegistrationHolder.getItemRegister(),
                        reg -> blockCtor.apply(props, reg),
                        MultiblockItem::new
                )
                .defaultBEs(IGRegistrationHolder.getTeRegister());
    }

    private static <S extends IMultiblockState> IGMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid)
    {
        Properties properties = Properties.of()
                .sound(SoundType.NETHER_BRICKS)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
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

    public static void forceClassLoad()
    {
        IGLib.IG_LOGGER.info("- Providing Multiblocks to Immersive Engineering -");
    };
}
