package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.renderer.multiblocks.CrystallizerRenderer;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEventHandlers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        //registerBERenderers(event);
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
