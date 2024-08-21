package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEventHandlers {

//    @SubscribeEvent
//    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
//        registerBERenderers(event);
//    }
//
//
//    public static void registerBERenderers(EntityRenderersEvent.RegisterRenderers event){
//        registerBERenderNoContext(event, () -> {return IGRegistrationHolder.MB_REGISTRY_MAP.get("crystallizer").masterBE();
//        }, CrystallizerRenderer::new);
//    }
//
//    private static <T extends BlockEntity> void registerBERenderNoContext(RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, Supplier<BlockEntityRenderer<T>> render)
//    {
//        registerBERenderNoContext(event, (BlockEntityType)type.get(), render);
//    }
//
//    private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render) {
//        event.registerBlockEntityRenderer(type, ($) -> {
//            return (BlockEntityRenderer)render.get();
//        });
//    }
}
