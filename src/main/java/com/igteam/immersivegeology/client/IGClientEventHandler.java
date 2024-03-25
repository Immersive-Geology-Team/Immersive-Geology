package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.client.render.MultiblockCrystallizerRenderer;
import com.igteam.immersivegeology.client.render.MultiblockGravitySeparatorRenderer;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGTileTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGClientEventHandler {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        registerBERender(event, IGTileTypes.CRYSTALLIZER.master(), MultiblockCrystallizerRenderer::new);
        registerBERender(event, IGTileTypes.GRAVITY_SEPARATOR.master(), MultiblockGravitySeparatorRenderer::new);
    }

    private static <T extends BlockEntity> void registerBERender(EntityRenderersEvent.RegisterRenderers ev, BlockEntityType<T> type, Supplier<BlockEntityRenderer<T>> factory){
        ev.registerBlockEntityRenderer(type, ctx -> factory.get());
    }
}
