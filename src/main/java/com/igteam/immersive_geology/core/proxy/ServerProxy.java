package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.world.IGWorldGeneration;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

public class ServerProxy implements Proxy {

    @Override
    public void onSetup(FMLCommonSetupEvent event) {
        IGWorldGeneration.initialize();
        MinecraftForge.EVENT_BUS.register(new IGWorldGeneration());
        DataSerializers.registerSerializer(IGFluid.OPTIONAL_FLUID_STACK);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {
   }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }

    @Override
    public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer){
    }

    protected static ResourceLocation modLoc(String str){
        return new ResourceLocation(IGLib.MODID, str);
    }
}
