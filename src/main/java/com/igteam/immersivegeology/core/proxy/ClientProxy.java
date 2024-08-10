package com.igteam.immersivegeology.core.proxy;

import blusunrize.immersiveengineering.client.ClientEventHandler;
import com.igteam.immersivegeology.client.renderer.multiblocks.CrystallizerRenderer;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

public class ClientProxy extends CommonProxy {
    @Override
    public void setup(){
    }


    @Override
    public void registerContainersAndScreens(RegisterMenuScreensEvent event) {
        // TODO
    }


    @Override
    public void completed(FMLLoadCompleteEvent event){
        // TODO IE Manual Entries Here
    }

    @Override
    public void preInit(){
    }

    @Override
    public void preInitEnd(){
    }

    @Override
    public void init(){
        //NeoForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
