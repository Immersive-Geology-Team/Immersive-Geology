package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public interface Proxy {

    void onSetup(final FMLCommonSetupEvent event);
    default void onFinishSetup(final FMLLoadCompleteEvent event){};
    default void onClientSetup(final FMLClientSetupEvent event){};
    default void onServerStarting(FMLServerStartingEvent event){};
    default void onServerStopped(FMLServerStoppedEvent event){};

    @SuppressWarnings({"unused", "RedundantSuppression"})
    default void onEnqueueModComs(final InterModEnqueueEvent event){};

    @SuppressWarnings({"unused", "RedundantSuppression"})
    default void onProcessModComs(final InterModProcessEvent event){};

    default void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer){};


}
