package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.lib.ResourceUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IGRegistrationHolder {
    private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.createBlocks(IGLib.MODID);
    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.createItems(IGLib.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IGLib.MODID);

    private static final List<Consumer<IEventBus>> MOD_BUS_CALLBACKS = new ArrayList<>();

    public static void addRegistersToEventBus(final IEventBus eventBus){
        BLOCK_REGISTER.register(eventBus);
        ITEM_REGISTER.register(eventBus);
        TE_REGISTER.register(eventBus);

        MOD_BUS_CALLBACKS.forEach(e -> e.accept(eventBus));
    }



    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure){
        BlockBehaviour.Properties prop = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
                .strength(3,15)
                .requiresCorrectToolForDrops()
                .isViewBlocking((state, blockReader, pos) -> false)
                .noOcclusion()
                .dynamicShape()
                .pushReaction(PushReaction.BLOCK);

        return registerMultiblock(name, logic, structure, prop);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, BlockBehaviour.Properties prop){
        final ResourceLocation rl = ResourceUtils.ig(name);

        MultiblockBuilder<S> builder = new MultiblockBuilder<>(logic, rl)
                .structure(structure)
                .defaultBEs(IGRegistrationHolder.TE_REGISTER)
                .defaultBlock(IGRegistrationHolder.BLOCK_REGISTER, IGRegistrationHolder.ITEM_REGISTER, prop);

        return builder.build(IGRegistrationHolder.MOD_BUS_CALLBACKS::add);
    }

    private static class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>> {
        public MultiblockBuilder(IMultiblockLogic<S> logic, ResourceLocation name){
            super(logic, name);
        }

        @Override
        protected MultiblockBuilder<S> self(){
            return this;
        }
    }
}
