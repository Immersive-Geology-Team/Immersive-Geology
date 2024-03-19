package com.igteam.immersivegeology.core.proxy;

import com.igteam.immersivegeology.client.renderer.multiblocks.CrystallizerRenderer;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public class ClientProxy extends CommonProxy {
    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        super.onCommonSetup(event);
    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        IGLib.IG_LOGGER.info("Starting Client Setup");
        // TODO Implement Render Handler and Creative Menu Handlers

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        registerBERenderers(event);
    }


    public static void registerBERenderers(EntityRenderersEvent.RegisterRenderers event){
        registerBERenderNoContext(event, IGMultiblockProvider.CRYSTALLIZER.masterBE(), CrystallizerRenderer::new);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, Supplier<BlockEntityRenderer<T>> render) {
        registerBERenderNoContext(event, (BlockEntityType)type.get(), render);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render) {
        event.registerBlockEntityRenderer(type, ($) -> {
            return (BlockEntityRenderer)render.get();
        });
    }
}
